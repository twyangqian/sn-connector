package com.thoughtworks.otr.snconnector.client.impl;

import com.julienvey.trello.impl.TrelloUrl;
import com.thoughtworks.otr.snconnector.client.TrelloCardClient;
import com.thoughtworks.otr.snconnector.client.TrelloClient;
import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;
import com.thoughtworks.otr.snconnector.constans.TrelloUrlConstant;
import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Component
public class TrelloCardClientImpl extends TrelloClient implements TrelloCardClient {
    public TrelloCardClientImpl(TrelloConfiguration trelloConfiguration) {
        super(trelloConfiguration);
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
}
