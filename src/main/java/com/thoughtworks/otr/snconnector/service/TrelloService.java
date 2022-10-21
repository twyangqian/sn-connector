package com.thoughtworks.otr.snconnector.service;

import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.client.impl.TrelloBoardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloCardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloListCardClientImpl;
import com.thoughtworks.otr.snconnector.dto.CreateTrelloCardDTO;
import com.thoughtworks.otr.snconnector.dto.ServiceNowEntryDTO;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrelloService {

    private static final String DEFAULT_TRELLO_CARD_LIST_NAME = "TODO";

    private final TrelloBoardClientImpl trelloBoardClient;
    private final TrelloListCardClientImpl trelloListCardClient;
    private final TrelloCardClientImpl trelloCardClient;

    public TrelloCard createTrelloCard(CreateTrelloCardDTO createTrelloCardDTO) {
        Optional<ServiceNowEntryDTO> serviceNowEntryDTO = createTrelloCardDTO.getServiceNowData().getEntries()
                                                                             .stream().findFirst();

        if (serviceNowEntryDTO.isEmpty()) {
            throw new TrelloException("no data in service now request body");
        }


        String newTrelloCardName = serviceNowEntryDTO.get().getDisplayValue() + " " +
                serviceNowEntryDTO.get().getShortDescription();

        String newTrelloCardDesc = serviceNowEntryDTO.get().getShortDescription();

        List<TList> trelloCardList = trelloBoardClient.getBoardListCards(createTrelloCardDTO.getTrelloBoardId());
        String trelloListCardId = getOrCreateTrelloCardListId(createTrelloCardDTO.getTrelloBoardId(), trelloCardList);
        log.info("TODO trello card list id is {}", trelloListCardId);

        checkBoardCardIsExists(newTrelloCardName, createTrelloCardDTO.getTrelloBoardId());
        TrelloCard newTrelloCard = TrelloCard.builder()
                                     .name(newTrelloCardName)
                                     .desc(newTrelloCardDesc)
                                     .idList(trelloListCardId)
                                     .build();
        return trelloCardClient.createCard(newTrelloCard);
    }

    private void checkBoardCardIsExists(String newTrelloCardName, String boardId) {
        if (trelloBoardClient.getBoardCards(boardId)
                        .stream()
                        .anyMatch(card -> card.getName().equals(newTrelloCardName))) {
            throw new TrelloException("this trello card has been created!");
        }
    }

    private String getOrCreateTrelloCardListId(String boardId, List<TList> trelloCardList) {
        return trelloCardList.stream()
                      .filter(trelloList -> trelloList.getName().equals(DEFAULT_TRELLO_CARD_LIST_NAME))
                      .findFirst()
                      .map(TList::getId)
                      .orElseGet(() -> trelloBoardClient.createBoardListCard(boardId, DEFAULT_TRELLO_CARD_LIST_NAME));
    }
}
