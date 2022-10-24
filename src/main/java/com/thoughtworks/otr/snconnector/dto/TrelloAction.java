package com.thoughtworks.otr.snconnector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrelloAction {
    private String id;
    private String idMemberCreator;

    @JsonProperty("data")
    private ActionData data;
    private String type;
    private String date;

    @Getter
    @Setter
    public static class ActionData {
        @JsonProperty("text")
        private String text;
    }
}
