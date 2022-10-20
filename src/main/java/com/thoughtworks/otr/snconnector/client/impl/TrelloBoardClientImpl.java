package com.thoughtworks.otr.snconnector.client.impl;

import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloUrl;
import com.thoughtworks.otr.snconnector.client.TrelloBoardClient;
import com.thoughtworks.otr.snconnector.client.TrelloClient;
import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;
import com.thoughtworks.otr.snconnector.constans.TrelloUrlConstant;
import com.thoughtworks.otr.snconnector.dto.CustomField;
import com.thoughtworks.otr.snconnector.dto.TrelloCardListDTO;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class TrelloBoardClientImpl extends TrelloClient implements TrelloBoardClient {

    public TrelloBoardClientImpl(TrelloConfiguration trelloConfiguration) {
        super(trelloConfiguration);
    }

    @Override
    public List<Member> getBoardMembers() {
        return super.getTrelloApi().getBoardMembers(super.getTrelloConfiguration().getTrelloBoardId());
    }

    @Override
    public List<Label> getBoardLabels() {
        return super.getTrelloApi().getBoardLabels(super.getTrelloConfiguration().getTrelloBoardId())
                            .stream()
                            .filter(label -> StringUtils.isNotBlank(label.getName()))
                            .collect(Collectors.toList());
    }

    @Override
    public List<CustomField> getBoardCustomFields() {
        String url = TrelloUrl.API_URL + TrelloUrlConstant.GET_BOARD_CUSTOM_FIELDS;
        URI fullUrl = buildFullURLBuilder(url)
                .buildAndExpand(super.getTrelloConfiguration().getTrelloBoardId())
                .toUri();
        CustomField[] customFields = super.getRestTemplate().getForObject(fullUrl, CustomField[].class);
        return Objects.nonNull(customFields) ? asList(customFields) : emptyList();
    }

    @Override
    public List<TList> getBoardListCards() {
        Board board = super.getTrelloApi().getBoard(super.getTrelloConfiguration().getTrelloBoardId());
        return board.fetchLists();
    }

    @Override
    public String createBoardListCard(String listCardName) {
        String url = TrelloUrl.API_URL + TrelloUrl.GET_BOARD_LISTS;
        URI fullUri = buildFullURLBuilder(url)
                            .queryParam("name", listCardName)
                            .buildAndExpand(super.getTrelloConfiguration().getTrelloBoardId()).toUri();

        TrelloCardListDTO trelloCardListDTO = super.getRestTemplate().postForObject(fullUri, null, TrelloCardListDTO.class);
        return Optional.ofNullable(trelloCardListDTO)
                       .orElseThrow(() ->  new TrelloException("create trello board failed!"))
                       .getId();
    }
}
