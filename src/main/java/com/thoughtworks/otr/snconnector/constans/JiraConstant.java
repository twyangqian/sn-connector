package com.thoughtworks.otr.snconnector.constans;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JiraConstant {

    public static final String JIRA_URL = "https://itsc-jira.mercedes-benz.com.cn/jira/";

    //10002 -> TASK ISSUE
    public static final Long TASK_ISSUE_TYPE = 10002L;

    public static final String OTR_PRODUCTION_PROJECT_KEY = "14502";

    public static final String PARTS_TASK_JIRA_DESCRIPTION_TEMPLATE = "jira/parts.ftl";

    public static final String TRELLO_CHECK_LIST_ROOT_CAUSE = "问题原因";
    public static final String TRELLO_CHECK_LIST_REPAIR_PLAN = "修复方式";
}
