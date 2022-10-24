package com.thoughtworks.otr.snconnector.service;

import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.client.impl.TrelloBoardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloCardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloListCardClientImpl;
import com.thoughtworks.otr.snconnector.dto.CreateTrelloCardDTO;
import com.thoughtworks.otr.snconnector.dto.CustomField;
import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.ServiceNowEntryDTO;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final TrelloBoardClientImpl trelloBoardClient;
    private final TrelloListCardClientImpl trelloListCardClient;
    private final TrelloCardClientImpl trelloCardClient;

    public TrelloCard createTrelloCard(CreateTrelloCardDTO createTrelloCardDTO) {
        List<ServiceNowEntryDTO> serviceNowEntryDTOS = createTrelloCardDTO.getServiceNowData().getEntries()
                                                              .stream()
                                                              .sorted(Comparator.comparing(ServiceNowEntryDTO::getSysCreatedOnAdjusted))
                                                              .collect(Collectors.toList());

        Optional<ServiceNowEntryDTO> earliestServiceNowEntry = serviceNowEntryDTOS.stream().findFirst();
        if (earliestServiceNowEntry.isEmpty()) {
            throw new TrelloException("no data in service now request body");
        }

        String ticketNumber = earliestServiceNowEntry.get().getDisplayValue();
        String ticketOpenDate = earliestServiceNowEntry.get().getSysCreatedOnAdjusted();
        String newTrelloCardName = ticketNumber + " " + earliestServiceNowEntry.get().getShortDescription();
        String newTrelloCardDesc = earliestServiceNowEntry.get().getShortDescription();

        List<TList> trelloListCards = trelloBoardClient.getBoardListCards(createTrelloCardDTO.getTrelloBoardId());
        String trelloListCardId = getOrCreateTODOTrelloListCard(createTrelloCardDTO.getTrelloBoardId(), trelloListCards);
        log.info("TODO trello list card id is {}", trelloListCardId);

        TrelloCard trelloCard = getOrCreateTrelloCard(createTrelloCardDTO, newTrelloCardName, newTrelloCardDesc, trelloListCardId);

        Map<String, CustomField> customFieldMap = buildBoardCustomFieldMap(createTrelloCardDTO);
        Map<String, CustomFieldItem> cardCustomFiledItemMap =
                customFieldMap.entrySet()
                              .stream()
                              .map(customField -> buildCustomFieldItem(ticketNumber, ticketOpenDate, customField))
                              .collect(Collectors.toMap(CustomFieldItem::getIdCustomField, Function.identity()));


        serviceNowEntryDTOS.forEach(entry -> {
            setValueForCardCustomFieldItem(customFieldMap, cardCustomFiledItemMap, entry);
        });

        updateCardCustomFieldItemValueByCallTrelloAPI(trelloCard, cardCustomFiledItemMap);


        return trelloCard;
    }

    private void updateCardCustomFieldItemValueByCallTrelloAPI(TrelloCard trelloCard, Map<String, CustomFieldItem> cardCustomFiledItemMap) {
        List<CustomFieldItem> cardCustomFieldItems = new ArrayList<>();
        cardCustomFiledItemMap.forEach((key, customFieldItem) -> {
                   if (Objects.nonNull(customFieldItem.getValue())) {
                       trelloCardClient.updateCardCustomFieldItem(
                               trelloCard.getId(), customFieldItem.getIdCustomField(), customFieldItem);
                       cardCustomFieldItems.add(customFieldItem);
                   }
                }
        );
        trelloCard.setCustomFieldItems(cardCustomFieldItems);
    }

    private void setValueForCardCustomFieldItem(Map<String, CustomField> customFieldMap,
                                                Map<String, CustomFieldItem> cardCustomFiledItemMap,
                                                ServiceNowEntryDTO entry) {
        entry.getEntries().getChanges().forEach(change -> {
                    CustomField customField = customFieldMap.get(change.getFieldName().getTrelloFieldName());
                    if (Objects.nonNull(customField)) {
                        cardCustomFiledItemMap.get(customField.getId())
                                              .setValue(Map.of(customField.getType(), change.getNewValue()));
                    }
                });
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

    private Map<String, CustomField> buildBoardCustomFieldMap(CreateTrelloCardDTO createTrelloCardDTO) {
        return trelloBoardClient.getBoardCustomFields(createTrelloCardDTO.getTrelloBoardId())
                                .stream()
                                .collect(Collectors.toMap(CustomField::getName, Function.identity()));
    }

    private TrelloCard getOrCreateTrelloCard(CreateTrelloCardDTO createTrelloCardDTO, String newTrelloCardName, String newTrelloCardDesc, String trelloListCardId) {
        TrelloCard trelloCard;
        TrelloCard oldTrelloCard = getBoardOldCard(newTrelloCardName, createTrelloCardDTO.getTrelloBoardId());

        if (Objects.nonNull(oldTrelloCard)) {
            trelloCard = oldTrelloCard;
        } else {
            trelloCard = trelloCardClient.createCard(TrelloCard.builder()
                                                             .name(newTrelloCardName)
                                                             .desc(newTrelloCardDesc)
                                                             .idList(trelloListCardId)
                                                             .build());
        }
        return trelloCard;
    }

    private TrelloCard getBoardOldCard(String newTrelloCardName, String boardId) {
        return trelloBoardClient.getBoardCards(boardId)
                        .stream()
                        .filter(card -> card.getName().equals(newTrelloCardName))
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
