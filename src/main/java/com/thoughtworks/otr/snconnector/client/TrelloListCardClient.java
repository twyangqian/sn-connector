package com.thoughtworks.otr.snconnector.client;

import com.julienvey.trello.domain.Card;

import java.util.List;

public interface TrelloListCardClient {
    List<Card> getListCardCards(String listCardId);
}
