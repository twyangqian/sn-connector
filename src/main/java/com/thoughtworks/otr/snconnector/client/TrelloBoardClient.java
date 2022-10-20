package com.thoughtworks.otr.snconnector.client;

import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.dto.CustomField;

import java.util.List;

public interface TrelloBoardClient {
    List<Member> getBoardMembers(String boardId);
    List<Label> getBoardLabels(String boardId);
    List<CustomField> getBoardCustomFields(String boardId);
    List<TList> getBoardListCards(String boardId);
    String createBoardListCard(String boardId, String listCardName);
}
