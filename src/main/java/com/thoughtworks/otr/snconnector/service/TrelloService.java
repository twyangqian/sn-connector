package com.thoughtworks.otr.snconnector.service;

import com.thoughtworks.otr.snconnector.client.TrelloClient;
import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;
import com.thoughtworks.otr.snconnector.dto.CreateTrelloCardDTO;
import com.thoughtworks.otr.snconnector.dto.ServiceNowEntryDTO;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import com.julienvey.trello.domain.TList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TrelloService {

    private static final String DEFAULT_TRELLO_CARD_LIST_NAME = "TODO";

    public TrelloCard createTrelloCard(CreateTrelloCardDTO createTrelloCardDTO) {
        log.info("trello api Key: {}, trello api token: {}, trello board id: {}",
                createTrelloCardDTO.getTrelloApiKey(),
                createTrelloCardDTO.getTrelloApiToken(),
                createTrelloCardDTO.getTrelloBoardId());

        Optional<ServiceNowEntryDTO> serviceNowEntryDTO = createTrelloCardDTO.getServiceNowData().getEntries()
                                                                             .stream().findFirst();

        if (serviceNowEntryDTO.isEmpty()) {
            throw new TrelloException("no data in service now request body");
        }

        String newTrelloCardName = serviceNowEntryDTO.get().getDisplayValue() + " " +
                serviceNowEntryDTO.get().getShortDescription();

        String newTrelloCardDesc = serviceNowEntryDTO.get().getShortDescription();

        TrelloConfiguration trelloConfiguration = TrelloConfiguration.builder()
                                                                     .trelloApiKey(createTrelloCardDTO.getTrelloApiKey())
                                                                     .trelloApiToken(createTrelloCardDTO.getTrelloApiToken())
                                                                     .trelloBoardId(createTrelloCardDTO.getTrelloBoardId())
                                                                     .build();
        TrelloClient trelloClient = new TrelloClient(trelloConfiguration);

        List<TList> trelloCardList = trelloClient.getCardListCollection();
        String trelloCardListId = getOrCreateTrelloCardListId(trelloClient, trelloCardList);
        log.info("TODO trello card list id is {}", trelloCardListId);

        checkCardIsExists(newTrelloCardName, trelloClient, trelloCardListId);

        TrelloCard newTrelloCard = TrelloCard.builder()
                                     .name(newTrelloCardName)
                                     .desc(newTrelloCardDesc)
                                     .idList(trelloCardListId)
                                     .build();
        return trelloClient.createCard(newTrelloCard);
    }

    private void checkCardIsExists(String newTrelloCardName, TrelloClient trelloClient, String trelloCardListId) {
        if (trelloClient.getListCards(trelloCardListId)
                        .stream()
                        .anyMatch(card -> card.getName().equals(newTrelloCardName))) {
            throw new TrelloException("this trello card has been created!");
        }
    }

    private String getOrCreateTrelloCardListId(TrelloClient trelloClient, List<TList> trelloCardList) {
        return trelloCardList.stream()
                      .filter(trelloList -> trelloList.getName().equals(DEFAULT_TRELLO_CARD_LIST_NAME))
                      .findFirst()
                      .map(TList::getId)
                      .orElseGet(() -> trelloClient.createCardList(DEFAULT_TRELLO_CARD_LIST_NAME));
    }
}
