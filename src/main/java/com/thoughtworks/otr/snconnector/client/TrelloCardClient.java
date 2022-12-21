package com.thoughtworks.otr.snconnector.client;

import com.julienvey.trello.domain.Attachment;
import com.thoughtworks.otr.snconnector.dto.CustomFieldItem;
import com.thoughtworks.otr.snconnector.dto.TrelloAction;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.dto.TrelloCardCheckList;

import java.util.List;

public interface TrelloCardClient {
    TrelloCard getCard(String cardId);
    TrelloCard createCard(TrelloCard card);
    List<CustomFieldItem> getCardCustomFieldItems(String cardId);
    void updateCardCustomFieldItem(String cardId, String customFiledItemId, CustomFieldItem updateCustomFieldItem);
    List<TrelloAction> getCardActions(String cardId);
    void createCardComment(String cardId, String commentText);
    void updateCard(TrelloCard card);
    List<TrelloCardCheckList> getCardCheckLists(String cardId);
    TrelloCardCheckList createCardCheckList(String cardId, TrelloCardCheckList trelloCardCheckList);
    Attachment createCardAttachment(String cardId, Attachment attachment);
}
