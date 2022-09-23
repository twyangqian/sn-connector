package com.daimler.otr.operationtools.dto;

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
public class TrelloCard {
    private String id;
    private String name;
    private String idList;
    private String desc;
    private List<String> idMembers;
    private List<String> idLabels;
}
