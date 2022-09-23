package com.daimler.otr.operationtools.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TrelloConfiguration {
    private String trelloApiKey;
    private String trelloApiToken;
    private String trelloBoardId;
}
