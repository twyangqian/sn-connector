package com.daimler.otr.operationtools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTrelloCardDTO {

    @NotBlank
    private String trelloApiKey;
    @NotBlank
    private String trelloApiToken;
    @NotBlank
    private String trelloBoardId;
    @NotBlank
    private ServiceNowDTO serviceNowData;
}
