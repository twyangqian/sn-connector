package com.thoughtworks.otr.snconnector.constans;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrelloUrlConstant {
    public static final String GET_BOARD_CUSTOM_FIELDS = "/boards/{id}/customFields?";

    public static final String GET_CARD_CUSTOM_FIELD_ITEMS = "/cards/{cardId}/customFieldItems?";
    public static final String UPDATE_CARD_CUSTOM_FIELD_ITEM = "/cards/{cardId}/customField/{customFieldId}/item?";
    public static final String GET_CARD_ACTIONS = "/cards/{id}/actions?";
}
