package com.thoughtworks.otr.snconnector.client.impl;

import com.julienvey.trello.impl.TrelloUrl;
import com.thoughtworks.otr.snconnector.client.TrelloCardClient;
import com.thoughtworks.otr.snconnector.client.TrelloClient;
import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;
import com.thoughtworks.otr.snconnector.constans.TrelloUrlConstant;
import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.TrelloAction;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.dto.TrelloCardCheckList;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.thoughtworks.otr.snconnector.utils.mapper.TrelloCardMapper.TRELLO_CARD_MAPPER;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Component
public class TrelloCardClientImpl extends TrelloClient implements TrelloCardClient {
    public TrelloCardClientImpl(TrelloConfiguration trelloConfiguration) {
        super(trelloConfiguration);
    }

    @Override
    public TrelloCard getCard(String cardId) {
        return TRELLO_CARD_MAPPER.toTrelloCard(super.getTrelloApi().getCard(cardId));
    }

    @Override
    public TrelloCard createCard(TrelloCard card) {
        String url = TrelloUrl.API_URL + TrelloUrl.CREATE_CARD;
        URI fullUri = buildFullURLBuilder(url)
                .build().toUri();

        return super.getRestTemplate().postForObject(fullUri, card, TrelloCard.class);
    }

    @Override
    public List<CustomFieldItem> getCardCustomFieldItems(String cardId) {
        String url = TrelloUrl.API_URL + TrelloUrlConstant.GET_CARD_CUSTOM_FIELD_ITEMS;
        URI fullUrl = buildFullURLBuilder(url)
                .buildAndExpand(cardId)
                .toUri();
        CustomFieldItem[] customFieldItems = super.getRestTemplate().getForObject(fullUrl, CustomFieldItem[].class);
        return Objects.nonNull(customFieldItems) ? asList(customFieldItems) : emptyList();
    }

    @Override
    public void updateCardCustomFieldItem(String cardId, String customFiledItemId, CustomFieldItem updateCustomFieldItem) {
        String url = TrelloUrl.API_URL + TrelloUrlConstant.UPDATE_CARD_CUSTOM_FIELD_ITEM;
        URI fullUrl = buildFullURLBuilder(url)
                .buildAndExpand(
                        Map.of(
                                "cardId", cardId,
                                "customFieldId", customFiledItemId))
                .toUri();
        super.getRestTemplate().put(fullUrl, updateCustomFieldItem);
    }

    @Override
    public List<TrelloAction> getCardActions(String cardId) {
        String url = TrelloUrl.API_URL + TrelloUrlConstant.GET_CARD_ACTIONS;
        URI fullUrl = buildFullURLBuilder(url)
                .buildAndExpand(cardId)
                .toUri();
        TrelloAction[] cardComments = super.getRestTemplate().getForObject(fullUrl, TrelloAction[].class);
        return Objects.nonNull(cardComments) ? asList(cardComments) : emptyList();
    }

    @Override
    public void createCardComment(String cardId, String commentText) {
        super.getTrelloApi().addCommentToCard(cardId, commentText);
    }

    @Override
    public void updateCard(TrelloCard card) {
        super.getTrelloApi().updateCard(card);
    }

    @Override
    public List<TrelloCardCheckList> getCardCheckLists(String cardId) {
        return super.getTrelloApi().getCardChecklists(cardId)
                .stream()
                .map(TRELLO_CARD_MAPPER::toTrelloCardCheckList)
                .collect(Collectors.toList());
    }

    @Override
    public TrelloCardCheckList createCardCheckList(String cardId, TrelloCardCheckList trelloCardCheckList) {
        return TRELLO_CARD_MAPPER.toTrelloCardCheckList(super.getTrelloApi().createCheckList(cardId, trelloCardCheckList));
    }
}
