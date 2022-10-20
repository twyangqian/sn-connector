package com.thoughtworks.otr.snconnector.client;

import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.dto.CustomField;

import java.util.List;

public interface TrelloBoardClient {
    List<Member> getBoardMembers();
    List<Label> getBoardLabels();
    List<CustomField> getBoardCustomFields();
    List<TList> getBoardListCards();
    String createBoardListCard(String listCardName);
}
