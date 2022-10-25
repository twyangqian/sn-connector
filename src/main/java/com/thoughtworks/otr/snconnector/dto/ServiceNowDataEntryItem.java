package com.thoughtworks.otr.snconnector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.otr.snconnector.enums.ServiceNowEntryFieldName;
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
public class ServiceNowDataEntryItem {

    @JsonProperty("field_label")
    private String fieldLabel;

    @JsonProperty("change_type")
    private String changeType;

    @JsonProperty("field_name")
    private ServiceNowEntryFieldName fieldName;

    @JsonProperty("old_value")
    private String oldValue;

    @JsonProperty("new_value")
    private String newValue;

}
