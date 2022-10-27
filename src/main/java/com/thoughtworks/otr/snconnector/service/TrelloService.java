package com.thoughtworks.otr.snconnector.service;

import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.client.impl.TrelloBoardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloCardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloListCardClientImpl;
import com.thoughtworks.otr.snconnector.dto.CustomField;
import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.ServiceNowData;
import com.thoughtworks.otr.snconnector.dto.ServiceNowDataEntry;
import com.thoughtworks.otr.snconnector.dto.TrelloAction;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.dto.TrelloCardComment;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thoughtworks.otr.snconnector.utils.mapper.TrelloCardMapper.TRELLO_CARD_MAPPER;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrelloService {

    private static final String DEFAULT_TRELLO_CARD_LIST_NAME = "TODO";
    private static final String SERVICE_NOW_LINK_TEMPLATE =
            "https://digitalservices.mercedes-benz.com/nav_to.do?uri=sn_customerservice_case.do?sys_id=%s";

    private final TrelloBoardClientImpl trelloBoardClient;
    private final TrelloListCardClientImpl trelloListCardClient;
    private final TrelloCardClientImpl trelloCardClient;

    public TrelloCard createTrelloCard(String boardId, ServiceNowData serviceNowData) {
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
        String newTrelloCardDesc = earliestServiceNowEntry.getShortDescription() + "\n" + buildServiceNowLink(earliestServiceNowEntry.getDocumentId());

        log.info("ticket number: {}, ticket open date: {}, trello card name: {}", ticketNumber, ticketOpenDate, newTrelloCardName);

        List<TList> trelloListCards = trelloBoardClient.getBoardListCards(boardId);
        String trelloListCardId = getOrCreateTODOTrelloListCard(boardId, trelloListCards);
        log.info("TODO trello list card id is {}", trelloListCardId);

        TrelloCard trelloCard = getOrCreateTrelloCard(boardId, ticketNumber, newTrelloCardName, newTrelloCardDesc, trelloListCardId);

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
                              .map(customField -> buildCustomFieldItem(ticketNumber, ticketOpenDate, customField))
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
        });

        createTrelloCardCustomFieldItem(cardCustomFiledItemMap, trelloCard, remoteCardCustomFieldItemsMap);

        return trelloCard;
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

    private CustomFieldItem buildCustomFieldItem(String ticket, String ticketOpenDate, Map.Entry<String, CustomField> customField) {
        CustomFieldItem customFieldItem = CustomFieldItem.builder()
                                              .idCustomField(customField.getValue().getId())
                                              .ModelType("card")
                                              .build();
        if (customField.getKey().equals("ticket")) {
            customFieldItem.setValue(Map.of(customField.getValue().getType(), ticket));
        } else if (customField.getKey().equals("ticket开启时间")) {
            customFieldItem.setValue(Map.of(customField.getValue().getType(), ticketOpenDate));
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

    private String getOrCreateTODOTrelloListCard(String boardId, List<TList> trelloCardList) {
        return trelloCardList.stream()
                      .filter(trelloList -> trelloList.getName().equals(DEFAULT_TRELLO_CARD_LIST_NAME))
                      .findFirst()
                      .map(TList::getId)
                      .orElseGet(() -> trelloBoardClient.createBoardListCard(boardId, DEFAULT_TRELLO_CARD_LIST_NAME));
    }
}
