package com.thoughtworks.otr.snconnector.client;

import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;

import java.util.List;

public interface TrelloCardClient {
    TrelloCard createCard(TrelloCard card);
    List<CustomFieldItem> getCardCustomFieldItems(String cardId);
    void updateCardCustomFieldItem(String cardId, String customFiledItemId, CustomFieldItem updateCustomFieldItem);
}
