package com.thoughtworks.otr.snconnector.client.impl;

import com.julienvey.trello.domain.Card;
import com.thoughtworks.otr.snconnector.client.TrelloClient;
import com.thoughtworks.otr.snconnector.client.TrelloListCardClient;
import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;

import java.util.List;

public class TrelloListCardClientImpl extends TrelloClient implements TrelloListCardClient {
    public TrelloListCardClientImpl(TrelloConfiguration trelloConfiguration) {
        super(trelloConfiguration);
    }

    @Override
    public List<Card> getListCardCards(String listCardId) {
        return super.getTrelloApi().getListCards(listCardId);
    }
}
