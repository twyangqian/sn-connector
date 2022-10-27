package com.thoughtworks.otr.snconnector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceNowDataEntry {

    @JsonProperty("display_value")
    private String displayValue;

    @JsonProperty("short_description")
    private String shortDescription;

    @JsonProperty("document_id")
    private String documentId;

    @JsonProperty("sys_created_on_adjusted")
    private String sysCreatedOnAdjusted;

    @JsonProperty("entries")
    private ServiceNowEntryEntries entries;

    @JsonProperty("sys_created_by")
    private String sysCreatedBy;


    @Getter
    @Setter
    public static class ServiceNowEntryEntries {
        @JsonProperty("journal")
        private List<ServiceNowDataEntryItem> journal;

        @JsonProperty("changes")
        private List<ServiceNowDataEntryItem> changes;
    }
}
