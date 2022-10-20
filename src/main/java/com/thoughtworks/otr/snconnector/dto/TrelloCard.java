package com.thoughtworks.otr.snconnector.dto;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrelloCard extends Card {
    private String id;
    private String name;
    private String idList;
    private String desc;
    private List<String> idMembers;
    private List<Label> labels;
}
