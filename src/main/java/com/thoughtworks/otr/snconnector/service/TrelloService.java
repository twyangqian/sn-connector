package com.thoughtworks.otr.snconnector.service;

import com.julienvey.trello.domain.CheckList;
import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.client.impl.TrelloBoardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloCardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloListCardClientImpl;
import com.thoughtworks.otr.snconnector.dto.CustomField;
import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.ServiceNowData;
import com.thoughtworks.otr.snconnector.dto.ServiceNowDataEntry;
import com.thoughtworks.otr.snconnector.dto.ServiceNowDataStatusChange;
import com.thoughtworks.otr.snconnector.dto.TrelloAction;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.dto.TrelloCardCheckList;
import com.thoughtworks.otr.snconnector.dto.TrelloCardComment;
import com.thoughtworks.otr.snconnector.enums.ServiceNowEntryFieldName;
import com.thoughtworks.otr.snconnector.enums.ServiceNowStatus;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import com.thoughtworks.otr.snconnector.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thoughtworks.otr.snconnector.utils.mapper.TrelloCardMapper.TRELLO_CARD_MAPPER;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrelloService {

    private static final String SERVICE_NOW_LINK_TEMPLATE =
            "https://digitalservices.mercedes-benz.com/nav_to.do?uri=sn_customerservice_case.do?sys_id=%s";

    private final TrelloBoardClientImpl trelloBoardClient;
    private final TrelloListCardClientImpl trelloListCardClient;
    private final TrelloCardClientImpl trelloCardClient;

    public TrelloCard createTrelloCard(String boardId, String defaultListCard, List<String> checkLists, ServiceNowData serviceNowData) {
        List<ServiceNowDataEntry> serviceNowDataEntries = serviceNowData.getEntries()
                                                                        .stream()
                                                                        .sorted(Comparator.comparing(ServiceNowDataEntry::getSysCreatedOnAdjusted))
                                                                        .collect(Collectors.toList());

        ServiceNowDataEntry earliestServiceNowEntry = serviceNowDataEntries.stream().findFirst().orElse(null);
        if (Objects.isNull(earliestServiceNowEntry)) {
            throw new TrelloException("no data in service now request body");
        }

        String ticketNumber = earliestServiceNowEntry.getDisplayValue();
        String ticketOpenDate = earliestServiceNowEntry.getSysCreatedOnAdjusted();
        String newTrelloCardName = ticketNumber + " " + earliestServiceNowEntry.getShortDescription();
        String newTrelloCardDesc = serviceNowData.getTicketFullDescription() + "\n" + buildServiceNowLink(earliestServiceNowEntry.getDocumentId());

        log.info("ticket number: {}, ticket open date: {}, trello card name: {}", ticketNumber, ticketOpenDate, newTrelloCardName);

        List<TList> trelloListCards = trelloBoardClient.getBoardListCards(boardId);
        String trelloListCardId = getOrCreateTODOTrelloListCard(boardId, defaultListCard, trelloListCards);
        log.info("TODO trello list card id is {}", trelloListCardId);

        TrelloCard trelloCard = getOrCreateTrelloCard(boardId, ticketNumber, newTrelloCardName, newTrelloCardDesc, trelloListCardId);

        createTrelloCardChecklists(trelloCard, checkLists);

        log.info("get trello card actions");
        Map<String, TrelloAction> trelloCardCommentsMap = trelloCardClient.getCardActions(trelloCard.getId())
                                                            .stream()
                                                            .filter(action -> action.getType().equals("commentCard"))
                                                            .collect(
                                                                    Collectors.toMap(
                                                                            trelloAction -> trelloAction.getData().getText(),
                                                                            Function.identity()));

        log.info("get trello board custom field");
        Map<String, CustomField> customFieldMap = buildBoardCustomFieldMap(boardId);

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

        updateTrelloCardDueDateWithSLA(trelloCard);

        return trelloCard;
    }

    private void createTrelloCardChecklists(TrelloCard trelloCard, List<String> checkLists) {
       List<String> remoteTrelloCardCheckList = trelloCardClient.getCardCheckLists(trelloCard.getId())
                                                                .stream()
                                                                .map(CheckList::getName)
                                                                .collect(Collectors.toList());

        checkLists.stream()
                .filter(checkListName -> !remoteTrelloCardCheckList.contains(checkListName))
                .forEach(checkListName -> {
                    TrelloCardCheckList trelloCardCheckList = trelloCardClient.createCardCheckList(
                            trelloCard.getId(), TrelloCardCheckList.builder()
                                                                   .idCard(trelloCard.getId())
                                                                   .name(checkListName)
                                                                   .build());
                    trelloCard.getCheckLists().add(trelloCardCheckList);
                });

    }

    private void updateTrelloCardDueDateWithSLA(TrelloCard trelloCard) {
        AtomicReference<Duration> ticketProcessTime = getTicketProcessTime(trelloCard);
        Date ticketDueDate = DateUtils.localDateTimeToDate(
                LocalDateTime.now().plus(Duration.ofHours(85).plus(Duration.ofMinutes(20)).minus(ticketProcessTime.get())));
        if (Objects.isNull(trelloCard.getDue()) || trelloCard.getDue().compareTo((ticketDueDate)) != 0) {
            trelloCard.setDue(ticketDueDate);
            trelloCardClient.updateCard(trelloCard);
        }
    }

    private AtomicReference<Duration> getTicketProcessTime(TrelloCard trelloCard) {
        AtomicReference<Duration> ticketProcessTime = new AtomicReference<>(Duration.ZERO);
        AtomicReference<ServiceNowDataStatusChange> previousStatusChange = new AtomicReference<>(
                trelloCard.getServiceNowDataStatusChanges()
                          .stream()
                          .findFirst()
                          .orElse(null));
        trelloCard.getServiceNowDataStatusChanges().forEach(
                currentStatusChange -> {
                    if (!previousStatusChange.get().getStatus().equals(ServiceNowStatus.AWAITING_INFO)) {
                        ticketProcessTime.set(ticketProcessTime.get().plus(DateUtils.betweenDurationInWorkDays(
                                DateUtils.stringToLocalDateTime(previousStatusChange.get().getStatusChangeData()),
                                DateUtils.stringToLocalDateTime(currentStatusChange.getStatusChangeData()))));
                    }
                    previousStatusChange.set(currentStatusChange);
                }
        );
        if (previousStatusChange.get().getStatus().equals(ServiceNowStatus.OPEN)) {
            ticketProcessTime.set(ticketProcessTime.get().plus(DateUtils.betweenDurationInWorkDays(
                    DateUtils.stringToLocalDateTime(previousStatusChange.get().getStatusChangeData()),
                    LocalDateTime.now())));
        }
        return ticketProcessTime;
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
        switch (customField.getKey()) {
            case "Ticket":
                customFieldItem.setValue(Map.of(customField.getValue().getType(), ticket));
                break;
            case "Ticket open date":
                customFieldItem.setValue(Map.of(customField.getValue().getType(), ticketOpenDate));
                break;
            case "Contact":
                customFieldItem.setValue(Map.of(customField.getValue().getType(), contactUserD8account));
                break;
            default:
                break;
        }
        return customFieldItem;
    }

    private Map<String, CustomField> buildBoardCustomFieldMap(String boardId) {
        return trelloBoardClient.getBoardCustomFields(boardId)
                                .stream()
                                .collect(Collectors.toMap(CustomField::getName, Function.identity()));
    }

    private TrelloCard getOrCreateTrelloCard(String boardId, String ticketNumber, String newTrelloCardName, String newTrelloCardDesc, String trelloListCardId) {
        TrelloCard trelloCard;
        TrelloCard oldTrelloCard = getBoardOldCard(ticketNumber, boardId);

        if (Objects.nonNull(oldTrelloCard)) {
            trelloCard = oldTrelloCard;
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

    private TrelloCard getBoardOldCard(String ticketNumber, String boardId) {
        return trelloBoardClient.getBoardCards(boardId)
                        .stream()
                        .filter(card -> card.getName().contains(ticketNumber))
                        .findFirst()
                        .map(TRELLO_CARD_MAPPER::toTrelloCard)
                        .orElse(null);
    }

    private String getOrCreateTODOTrelloListCard(String boardId, String defaultListCard, List<TList> trelloCardList) {
        return trelloCardList.stream()
                      .filter(trelloList -> trelloList.getName().equals(defaultListCard))
                      .findFirst()
                      .map(TList::getId)
                      .orElseGet(() -> trelloBoardClient.createBoardListCard(boardId, defaultListCard));
    }
}
