package com.daimler.otr.operationtools.service;

import com.daimler.otr.operationtools.client.TrelloClient;
import com.daimler.otr.operationtools.configuration.TrelloConfiguration;
import com.daimler.otr.operationtools.dto.CreateTrelloCardDTO;
import com.daimler.otr.operationtools.dto.ServiceNowEntryDTO;
import com.daimler.otr.operationtools.dto.TrelloCard;
import com.daimler.otr.operationtools.exception.TrelloException;
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

        TrelloCard newTrelloCard = TrelloCard.builder()
                                     .name(newTrelloCardName)
                                     .desc(newTrelloCardDesc)
                                     .idList(trelloCardListId)
                                     .build();
        return trelloClient.createCard(newTrelloCard);
    }

    private String getOrCreateTrelloCardListId(TrelloClient trelloClient, List<TList> trelloCardList) {
        return trelloCardList.stream()
                      .filter(trelloList -> trelloList.getName().equals(DEFAULT_TRELLO_CARD_LIST_NAME))
                      .findFirst()
                      .map(TList::getId)
                      .orElseGet(() -> trelloClient.createCardList(DEFAULT_TRELLO_CARD_LIST_NAME));
    }
}
