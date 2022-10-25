package com.thoughtworks.otr.snconnector.client.impl;

import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloUrl;
import com.thoughtworks.otr.snconnector.client.TrelloBoardClient;
import com.thoughtworks.otr.snconnector.client.TrelloClient;
import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;
import com.thoughtworks.otr.snconnector.constans.TrelloUrlConstant;
import com.thoughtworks.otr.snconnector.dto.CustomField;
import com.thoughtworks.otr.snconnector.dto.TrelloListCard;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Component
public class TrelloBoardClientImpl extends TrelloClient implements TrelloBoardClient {

    public TrelloBoardClientImpl(TrelloConfiguration trelloConfiguration) {
        super(trelloConfiguration);
    }

    @Override
    public List<Card> getBoardCards(String boardId) {
        return super.getTrelloApi().getBoardCards(boardId);
    }

    @Override
    public List<Member> getBoardMembers(String boardId) {
        return super.getTrelloApi().getBoardMembers(boardId);
    }

    @Override
    public List<Label> getBoardLabels(String boardId) {
        return super.getTrelloApi().getBoardLabels(boardId)
                            .stream()
                            .filter(label -> StringUtils.isNotBlank(label.getName()))
                            .collect(Collectors.toList());
    }

    @Override
    public List<CustomField> getBoardCustomFields(String boardId) {
        String url = TrelloUrl.API_URL + TrelloUrlConstant.GET_BOARD_CUSTOM_FIELDS;
        URI fullUrl = buildFullURLBuilder(url)
                            .buildAndExpand(boardId)
                            .toUri();
        CustomField[] customFields = super.getRestTemplate().getForObject(fullUrl, CustomField[].class);
        return Objects.nonNull(customFields) ? asList(customFields) : emptyList();
    }

    @Override
    public List<TList> getBoardListCards(String boardId) {
        Board board = super.getTrelloApi().getBoard(boardId);
        return board.fetchLists();
    }

    @Override
    public String createBoardListCard(String boardId, String listCardName) {
        String url = TrelloUrl.API_URL + TrelloUrl.GET_BOARD_LISTS;
        URI fullUri = buildFullURLBuilder(url)
                            .queryParam("name", listCardName)
                            .buildAndExpand(boardId)
                            .toUri();

        TrelloListCard trelloListCard = super.getRestTemplate().postForObject(fullUri, null, TrelloListCard.class);
        return Optional.ofNullable(trelloListCard)
                       .orElseThrow(() ->  new TrelloException("create trello board failed!"))
                       .getId();
    }
}
