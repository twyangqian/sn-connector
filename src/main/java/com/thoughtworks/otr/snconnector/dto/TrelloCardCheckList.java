package com.thoughtworks.otr.snconnector.dto;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.CheckItem;
import com.julienvey.trello.domain.CheckList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TrelloCardCheckList extends CheckList {
    private String id;
    private String name;
    private String idBoard;
    private String idCard;
    private int position;
    private List<CheckItem> checkItems;
    private List<Card> cards;
    private int pos;
}
