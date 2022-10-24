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
public class CustomField {
    private String id;
    private String idModel;
    private String modelType;
    private String fieldGroup;
    private String type;

    @JsonProperty("display")
    private Object display;

    @JsonProperty("name")
    private String name;

    @JsonProperty("pos")
    private String pos;
}
