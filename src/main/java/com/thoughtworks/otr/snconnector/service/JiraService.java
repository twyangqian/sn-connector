package com.thoughtworks.otr.snconnector.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.julienvey.trello.domain.CheckItem;
import com.thoughtworks.otr.snconnector.client.JiraClient;
import com.thoughtworks.otr.snconnector.client.impl.TrelloCardClientImpl;
import com.thoughtworks.otr.snconnector.dto.TrelloCardCheckList;
import com.thoughtworks.otr.snconnector.dto.jira.JiraRequest;
import com.thoughtworks.otr.snconnector.entity.ServiceNowSyncData;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import com.thoughtworks.otr.snconnector.repository.ServiceNowSyncDataRepository;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.thoughtworks.otr.snconnector.constans.JiraConstant.JIRA_URL;
import static com.thoughtworks.otr.snconnector.constans.JiraConstant.OTR_PRODUCTION_PROJECT_KEY;
import static com.thoughtworks.otr.snconnector.constans.JiraConstant.PARTS_TASK_JIRA_DESCRIPTION_TEMPLATE;
import static com.thoughtworks.otr.snconnector.constans.JiraConstant.TASK_ISSUE_TYPE;
import static com.thoughtworks.otr.snconnector.constans.JiraConstant.TRELLO_CHECK_LIST_REPAIR_PLAN;
import static com.thoughtworks.otr.snconnector.constans.JiraConstant.TRELLO_CHECK_LIST_ROOT_CAUSE;
import static com.thoughtworks.otr.snconnector.utils.FreeTemplateUtils.renderQueryWithTemplate;

@Service
@Slf4j
public class JiraService {

    private final ServiceNowSyncDataRepository syncDataRepository;
    private final TrelloCardClientImpl trelloCardClient;
    private final Configuration cfg;

    public JiraService(ServiceNowSyncDataRepository syncDataRepository, TrelloCardClientImpl trelloCardClient, Configuration cfg) {
        this.syncDataRepository = syncDataRepository;
        this.trelloCardClient = trelloCardClient;
        this.cfg = cfg;
    }

    public String createIssue(JiraRequest jiraRequest) {
        ServiceNowSyncData syncData = syncDataRepository.findByTicket(jiraRequest.getTicketNumber()).orElseThrow(
                () -> new TrelloException("未查找到相应的service now sync数据， ticket: " + jiraRequest.getTicketNumber())
        );

        List<TrelloCardCheckList> cardCheckLists = trelloCardClient.getCardCheckLists(syncData.getTrelloCardId());


        String jiraDescription = renderQueryWithTemplate(generateMapModel(syncData, cardCheckLists), cfg, PARTS_TASK_JIRA_DESCRIPTION_TEMPLATE);

        JiraClient jiraClient = new JiraClient(jiraRequest.getUsername(), jiraRequest.getPassword(), JIRA_URL);
        IssueRestClient issueClient = jiraClient.getJiraRestClient().getIssueClient();
        ProjectRestClient projectClient = jiraClient.getJiraRestClient().getProjectClient();

        Project otrProductionProject = projectClient.getProject(OTR_PRODUCTION_PROJECT_KEY).claim();

        IssueInput newIssue = new IssueInputBuilder(OTR_PRODUCTION_PROJECT_KEY, TASK_ISSUE_TYPE, jiraRequest.getSummary())
                .setProject(otrProductionProject)
                .setDescription(jiraDescription)
                .build();
        String key = issueClient.createIssue(newIssue).claim().getKey();
        return null;
    }

    private Map<String, String> generateMapModel(ServiceNowSyncData syncData, List<TrelloCardCheckList> cardCheckLists) {
        return Map.of(
                "ticket", syncData.getTicket(),
                "ticketDescription", syncData.getDescription(),
                "rootCause", getCheckListValue(cardCheckLists, TRELLO_CHECK_LIST_ROOT_CAUSE),
                "repairPlan", getCheckListValue(cardCheckLists, TRELLO_CHECK_LIST_REPAIR_PLAN)
                );
    }

    private String getCheckListValue(List<TrelloCardCheckList> cardCheckLists, String trelloCheckListName) {
        return cardCheckLists.stream()
                             .filter(checklist -> checklist.getName().equals(trelloCheckListName))
                             .findFirst()
                             .map(checklist -> checklist.getCheckItems()
                                                        .stream()
                                                        .map(CheckItem::getName)
                                                        .reduce((a, b) -> a.concat(b + "\n"))
                                                        .orElse("待补充"))
                             .orElse("待补充");
    }
}
