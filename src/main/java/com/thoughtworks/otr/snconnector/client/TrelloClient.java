package com.thoughtworks.otr.snconnector.client;

import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.dto.TrelloCardListDTO;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.TrelloUrl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrelloClient {
    private final TrelloConfiguration trelloConfiguration;
    private final Trello trelloApi;
    private final RestTemplate restTemplate;

    public TrelloClient(TrelloConfiguration trelloConfiguration) {
        this.trelloConfiguration = trelloConfiguration;
        this.trelloApi = new TrelloImpl(trelloConfiguration.getTrelloApiKey(), trelloConfiguration.getTrelloApiToken(), new JDKTrelloHttpClient());
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(8000);
        httpRequestFactory.setReadTimeout(10000);
        restTemplate = new RestTemplate(httpRequestFactory);
    }

    public List<TList> getCardListCollection() {
        Board board = trelloApi.getBoard(trelloConfiguration.getTrelloBoardId());
        return board.fetchLists();
    }

    public String createCardList(String trelloListName) {
        String url = TrelloUrl.API_URL + TrelloUrl.GET_BOARD_LISTS;
        URI fullUri = UriComponentsBuilder.fromUriString(url)
                                          .queryParam("name", trelloListName)
                                          .queryParam("key", trelloConfiguration.getTrelloApiKey())
                                          .queryParam("token", trelloConfiguration.getTrelloApiToken())
                                          .buildAndExpand(trelloConfiguration.getTrelloBoardId()).toUri();

        TrelloCardListDTO trelloCardListDTO = restTemplate.postForObject(fullUri, null, TrelloCardListDTO.class);
        return Optional.ofNullable(trelloCardListDTO)
                       .orElseThrow(() ->  new TrelloException("create trello board failed!"))
                       .getId();

    }

    public List<Member> getBoardMembers() {
        return trelloApi.getBoardMembers(trelloConfiguration.getTrelloBoardId());
    }

    public TrelloCard createCard(TrelloCard card) {
        String url = TrelloUrl.API_URL + TrelloUrl.CREATE_CARD;
        URI fullUri = UriComponentsBuilder.fromUriString(url)
                                          .queryParam("key", trelloConfiguration.getTrelloApiKey())
                                          .queryParam("token", trelloConfiguration.getTrelloApiToken())
                                          .build().toUri();

        return restTemplate.postForObject(fullUri, card, TrelloCard.class);
    }

    public List<Card> getListCards(String cardListId) {
        return trelloApi.getListCards(cardListId);
    }

    public List<Label> getLabels() {
        return trelloApi.getBoardLabels(trelloConfiguration.getTrelloBoardId()).stream()
                        .filter(label -> StringUtils.isNotBlank(label.getName()))
                        .collect(Collectors.toList());
    }
}
