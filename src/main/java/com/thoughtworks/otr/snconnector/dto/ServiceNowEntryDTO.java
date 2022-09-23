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
public class ServiceNowEntryDTO {

    @JsonProperty("display_value")
    private String displayValue;

    @JsonProperty("short_description")
    private String shortDescription;
}
