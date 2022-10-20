package com.thoughtworks.otr.snconnector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomFieldItem {

    private String id;
    private String cardId;
    private Map<String, String> value;
    private String idCustomField;
    private String idModel;
    private String ModelType;
}
