package com.thoughtworks.otr.snconnector.service;

import com.julienvey.trello.domain.CheckList;
import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.client.impl.TrelloBoardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloCardClientImpl;
import com.thoughtworks.otr.snconnector.dto.CustomField;
import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.ServiceNowData;
import com.thoughtworks.otr.snconnector.dto.ServiceNowDataEntry;
import com.thoughtworks.otr.snconnector.dto.ServiceNowDataStatusChange;
import com.thoughtworks.otr.snconnector.dto.TrelloAction;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.dto.TrelloCardCheckList;
import com.thoughtworks.otr.snconnector.dto.TrelloCardComment;
import com.thoughtworks.otr.snconnector.entity.ServiceNowSyncData;
import com.thoughtworks.otr.snconnector.entity.TrelloConfig;
import com.thoughtworks.otr.snconnector.entity.TrelloConfigCheckList;
import com.thoughtworks.otr.snconnector.enums.CustomFieldItemName;
import com.thoughtworks.otr.snconnector.enums.ServiceNowEntryFieldName;
import com.thoughtworks.otr.snconnector.enums.ServiceNowStatus;
import com.thoughtworks.otr.snconnector.enums.Squad;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import com.thoughtworks.otr.snconnector.repository.ServiceNowSyncDataRepository;
import com.thoughtworks.otr.snconnector.repository.TrelloConfigRepository;
import com.thoughtworks.otr.snconnector.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.thoughtworks.otr.snconnector.constans.ServiceNowConstant.SERVICE_NOW_LINK_TEMPLATE;
import static com.thoughtworks.otr.snconnector.constans.ServiceNowConstant.SERVICE_NOW_SLA_TABLE_TEMPLATE;
import static com.thoughtworks.otr.snconnector.constans.ServiceNowConstant.SLA_TIME_LEFT_REGEX;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrelloService {

    private final TrelloBoardClientImpl trelloBoardClient;
    private final TrelloCardClientImpl trelloCardClient;
    private final TrelloConfigRepository trelloConfigRepository;
    private final ServiceNowSyncDataRepository syncDataRepository;

    public TrelloCard createTrelloCard(Squad squad, ServiceNowData serviceNowData) {
        List<ServiceNowDataEntry> serviceNowDataEntries = serviceNowData.getEntries()
                                                                        .stream()
                                                                        .sorted(Comparator.comparing(ServiceNowDataEntry::getSysCreatedOnAdjusted))
                                                                        .collect(Collectors.toList());

        ServiceNowDataEntry earliestServiceNowEntry = serviceNowDataEntries.stream().findFirst().orElse(null);
        if (Objects.isNull(earliestServiceNowEntry)) {
            throw new TrelloException("no data in service now request body");
        }

        TrelloConfig trelloConfig = trelloConfigRepository.findBySquad(squad)
                                                          .orElseThrow(() -> new TrelloException(String.format("can not found trello config by %s squad", squad.name())));

        String ticketNumber = earliestServiceNowEntry.getDisplayValue();
        String ticketOpenDate = earliestServiceNowEntry.getSysCreatedOnAdjusted();
        String newTrelloCardName = ticketNumber + " " + earliestServiceNowEntry.getShortDescription();
        String newTrelloCardDesc = buildTrelloDescription(serviceNowData, earliestServiceNowEntry);

        log.info("ticket number: {}, ticket open date: {}, trello card name: {}", ticketNumber, ticketOpenDate, newTrelloCardName);

        List<TList> trelloListCards = trelloBoardClient.getBoardListCards(trelloConfig.getTrelloBoardId());
        String trelloListCardId = getOrCreateTODOTrelloListCard(trelloConfig.getTrelloBoardId(), trelloConfig.getDefaultListCardName(), trelloListCards);
        log.info("TODO trello list card id is {}", trelloListCardId);

        TrelloCard trelloCard = getOrCreateTrelloCard(ticketNumber, newTrelloCardName, newTrelloCardDesc, trelloListCardId);

        createTrelloCardChecklists(trelloCard, trelloConfig.getTrelloConfigCheckLists());

        log.info("get trello card actions");
        Map<String, TrelloAction> trelloCardCommentsMap = trelloCardClient.getCardActions(trelloCard.getId())
                                                            .stream()
                                                            .filter(action -> action.getType().equals("commentCard"))
                                                            .collect(
                                                                    Collectors.toMap(
                                                                            trelloAction -> trelloAction.getData().getText(),
                                                                            Function.identity()));

        log.info("get trello board custom field");
        Map<String, CustomField> customFieldMap = buildBoardCustomFieldMap(trelloConfig.getTrelloBoardId());

        log.info("build trello card custom field item");
        Map<String, CustomFieldItem> cardCustomFiledItemMap =
                customFieldMap.entrySet()
                              .stream()
                              .map(customField -> buildCustomFieldItem(ticketNumber, ticketOpenDate, serviceNowData.getContactUserD8Account(), customField))
                              .collect(Collectors.toMap(CustomFieldItem::getIdCustomField, Function.identity()));

        log.info("get remote trello card custom field items");
        Map<String, String> remoteCardCustomFieldItemsMap = trelloCardClient.getCardCustomFieldItems(trelloCard.getId())
                                                               .stream()
                                                               .collect(
                                                                       Collectors.toMap(
                                                                               CustomFieldItem::getIdCustomField,
                                                                               customFieldItem -> customFieldItem.getValue().values()
                                                                                                                 .stream()
                                                                                                                 .reduce(String::concat)
                                                                                                                 .orElse(null)));

        log.info("create trello card custom field item and comments");
        serviceNowDataEntries.forEach(entry -> {
            buildTrelloCardCustomFieldItem(customFieldMap, cardCustomFiledItemMap, entry);
            createTrelloCardComments(trelloCard, trelloCardCommentsMap, entry);
            buildServiceNowStatusChanges(entry, trelloCard);
        });

        createTrelloCardCustomFieldItem(cardCustomFiledItemMap, trelloCard, remoteCardCustomFieldItemsMap);

        updateTrelloCardDueDateWithSLA(trelloCard, serviceNowData.getSla().getBusinessTimeLeft());

        saveServiceNowSyncData(ticketNumber, earliestServiceNowEntry, serviceNowData, ticketOpenDate, trelloConfig, trelloCard);

        return trelloCard;
    }

    private String buildTrelloDescription(ServiceNowData serviceNowData, ServiceNowDataEntry earliestServiceNowEntry) {
        return serviceNowData.getTicketFullDescription() + "\n"
                + buildServiceNowLink(earliestServiceNowEntry.getDocumentId()) + "\n\n"
                + buildSLATable(serviceNowData);
    }

    private String buildSLATable(ServiceNowData serviceNowData) {
        return String.format(SERVICE_NOW_SLA_TABLE_TEMPLATE,
                serviceNowData.getSla().getBusinessTimeLeft(),
                serviceNowData.getSla().getBusinessElapsed(),
                serviceNowData.getSla().getBusinessElapsedPercentage());
    }

    private void saveServiceNowSyncData(String ticketNumber, ServiceNowDataEntry earliestServiceNowEntry, ServiceNowData serviceNowData, String ticketOpenDate, TrelloConfig trelloConfig, TrelloCard trelloCard) {
        syncDataRepository.findByTrelloCardId(trelloCard.getId()).ifPresentOrElse(
                syncData -> log.info("ticket {} service now sync data is exists", syncData.getTicket()),
                () -> syncDataRepository.save(
                        ServiceNowSyncData.builder()
                                          .ticket(ticketNumber)
                                          .shortDescription(earliestServiceNowEntry.getShortDescription())
                                          .description(serviceNowData.getTicketFullDescription())
                                          .serviceNowLink(buildServiceNowLink(earliestServiceNowEntry.getDocumentId()))
                                          .contact(serviceNowData.getContactUserD8Account())
                                          .ticketOpenDate(DateUtils.stringToInstant(ticketOpenDate))
                                          .squad(trelloConfig.getSquad())
                                          .trelloCardId(trelloCard.getId())
                                          .build())
                );
    }

    private void createTrelloCardChecklists(TrelloCard trelloCard, List<TrelloConfigCheckList> checkLists) {
       List<String> remoteTrelloCardCheckList = trelloCardClient.getCardCheckLists(trelloCard.getId())
                                                                .stream()
                                                                .map(CheckList::getName)
                                                                .collect(Collectors.toList());
       Collections.reverse(checkLists);
       checkLists.stream()
                 .filter(checkListName -> !remoteTrelloCardCheckList.contains(checkListName.getCheckListName()))
                 .forEach(checkListName -> {
                     TrelloCardCheckList trelloCardCheckList = trelloCardClient.createCardCheckList(
                             trelloCard.getId(), TrelloCardCheckList.builder()
                                                                   .idCard(trelloCard.getId())
                                                                   .name(checkListName.getCheckListName())
                                                                   .build());
                     trelloCard.getCheckLists().add(trelloCardCheckList);
                 });

    }

    private void updateTrelloCardDueDateWithSLA(TrelloCard trelloCard, String businessTimeLeft) {
        Duration ticketSLATimeLeft = parseTicketSLATimeLeft(businessTimeLeft);
        Date ticketDueDate = DateUtils.getDueDateByDurationFromNowInWorkDays(ticketSLATimeLeft);
        if (Objects.isNull(trelloCard.getDue()) || trelloCard.getDue().compareTo((ticketDueDate)) != 0) {
            trelloCard.setDue(ticketDueDate);
            trelloCardClient.updateCard(trelloCard);
        }
    }

    public Duration parseTicketSLATimeLeft(String businessTimeLeft) {
        // businessTimeLeft -> 1 Day 20 Hours 30 Minutes
        Pattern pattern = Pattern.compile(SLA_TIME_LEFT_REGEX);
        Matcher matcher = pattern.matcher(businessTimeLeft);
        if (matcher.matches()) {
            AtomicReference<String> days = new AtomicReference<>("0");
            AtomicReference<String> hours = new AtomicReference<>("0");
            AtomicReference<String> minutes = new AtomicReference<>("0");
            IntStream.rangeClosed(1, matcher.groupCount())
                    .forEach(index -> {
                        if (Objects.nonNull(matcher.group(index))) {
                            String extractTime = matcher.group(index).replace(" ", "");
                            if (extractTime.contains("Day")) {
                                days.set(extractTime.replace("Day", ""));
                            } else if (extractTime.contains("Hours")) {
                                hours.set(extractTime.replace("Hours", ""));
                            } else {
                                minutes.set(extractTime.replace("Minutes", ""));
                            }
                        }
                    });
            return Duration.parse(String.format("P%sDT%sH%sM", days.get(), hours.get(), minutes.get()));
        }
        log.error("parse business time left error {}", businessTimeLeft);
        return Duration.ZERO;
    }

    private void buildServiceNowStatusChanges(ServiceNowDataEntry entry, TrelloCard trelloCard) {
        entry.getEntries().getChanges().stream()
                .filter(change -> change.getFieldName().equals(ServiceNowEntryFieldName.STATE))
                .forEach(change -> trelloCard.getServiceNowDataStatusChanges().add(
                        ServiceNowDataStatusChange.builder()
                                                  .status(ServiceNowStatus.getServiceNowStatus(change.getNewValue()))
                                                  .statusChangeData(entry.getSysCreatedOnAdjusted())
                                                  .build()
                ));
    }

    private String buildServiceNowLink(String documentId) {
        return String.format(SERVICE_NOW_LINK_TEMPLATE, documentId);
    }


    private void createTrelloCardComments(TrelloCard trelloCard, Map<String, TrelloAction> trelloActionMap, ServiceNowDataEntry entry) {
        entry.getEntries().getJournal().forEach(journal -> {
            TrelloCardComment trelloCardComment = TrelloCardComment.builder()
                                                       .createdBy(entry.getSysCreatedBy())
                                                       .createdDate(entry.getSysCreatedOnAdjusted())
                                                       .commentType(journal.getFieldName().getLabel())
                                                       .commentText(journal.getNewValue())
                                                       .build();
            String commentText = trelloCardComment.buildTrelloCardCommentText();
            if (Objects.isNull(trelloActionMap.get(commentText))) {
                trelloCardClient.createCardComment(trelloCard.getId(), commentText);
            }
            trelloCard.getTrelloCardComments().add(trelloCardComment);
        });
    }

    private void buildTrelloCardCustomFieldItem(Map<String, CustomField> customFieldMap,
                                                Map<String, CustomFieldItem> cardCustomFiledItemMap,
                                                ServiceNowDataEntry entry) {
        entry.getEntries().getChanges().forEach(change -> {
                    CustomField customField = customFieldMap.get(change.getFieldName().getTrelloFieldName());
                    if (Objects.nonNull(customField)) {
                        cardCustomFiledItemMap.get(customField.getId())
                                              .setValue(Map.of(customField.getType(), change.getNewValue()));
                    }
                });
    }

    private void createTrelloCardCustomFieldItem(Map<String, CustomFieldItem> cardCustomFiledItemMap,
                                                 TrelloCard trelloCard,
                                                 Map<String, String> remoteCardCustomFieldItemsMap) {
        cardCustomFiledItemMap.values().forEach(customFieldItem -> {
                    if (Objects.nonNull(customFieldItem.getValue()) &&
                            customFieldItemValueChanges(customFieldItem, remoteCardCustomFieldItemsMap)) {
                        trelloCardClient.updateCardCustomFieldItem(
                                trelloCard.getId(), customFieldItem.getIdCustomField(), customFieldItem);
                        trelloCard.getCustomFieldItems().add(customFieldItem);
                    }
                }
        );
    }

    private boolean customFieldItemValueChanges(CustomFieldItem customFieldItem,
                                                Map<String, String> remoteCardCustomFieldItemsMap) {
        return !remoteCardCustomFieldItemsMap.containsKey(customFieldItem.getIdCustomField()) ||
                !remoteCardCustomFieldItemsMap.get(customFieldItem.getIdCustomField())
                                             .equals(customFieldItem.getValue().values()
                                                                    .stream()
                                                                    .reduce(String::concat)
                                                                    .orElse(null));
    }

    private CustomFieldItem buildCustomFieldItem(String ticket, String ticketOpenDate, String contactUserD8account, Map.Entry<String, CustomField> customField) {
        CustomFieldItem customFieldItem = CustomFieldItem.builder()
                                              .idCustomField(customField.getValue().getId())
                                              .ModelType("card")
                                              .build();
        if (customField.getKey().equals(CustomFieldItemName.TICKET.getValue())) {
            customFieldItem.setValue(Map.of(customField.getValue().getType(), ticket));
        } else if (customField.getKey().equals(CustomFieldItemName.TICKET_OPEN_DATE.getValue())) {
            customFieldItem.setValue(Map.of(customField.getValue().getType(), ticketOpenDate));
        } else if (customField.getKey().equals(CustomFieldItemName.CONTACT.getValue()) && Objects.nonNull(contactUserD8account)) {
            customFieldItem.setValue(Map.of(customField.getValue().getType(), contactUserD8account));
        }
        return customFieldItem;
    }

    private Map<String, CustomField> buildBoardCustomFieldMap(String boardId) {
        return trelloBoardClient.getBoardCustomFields(boardId)
                                .stream()
                                .collect(Collectors.toMap(CustomField::getName, Function.identity()));
    }

    private TrelloCard getOrCreateTrelloCard(String ticketNumber, String newTrelloCardName, String newTrelloCardDesc, String trelloListCardId) {
        TrelloCard trelloCard;
        Optional<ServiceNowSyncData> serviceNowSyncData = syncDataRepository.findByTicket(ticketNumber);

        if (serviceNowSyncData.isPresent()) {
            trelloCard = trelloCardClient.getCard(serviceNowSyncData.get().getTrelloCardId());
            trelloCard.setDesc(newTrelloCardDesc);
        } else {
            log.info("create trello card: {}", newTrelloCardName);
            trelloCard = trelloCardClient.createCard(TrelloCard.builder()
                                                             .name(newTrelloCardName)
                                                             .desc(newTrelloCardDesc)
                                                             .idList(trelloListCardId)
                                                             .build());
        }
        return trelloCard;
    }

    private String getOrCreateTODOTrelloListCard(String boardId, String defaultListCard, List<TList> trelloCardList) {
        return trelloCardList.stream()
                      .filter(trelloList -> trelloList.getName().equals(defaultListCard))
                      .findFirst()
                      .map(TList::getId)
                      .orElseGet(() -> trelloBoardClient.createBoardListCard(boardId, defaultListCard));
    }
}
