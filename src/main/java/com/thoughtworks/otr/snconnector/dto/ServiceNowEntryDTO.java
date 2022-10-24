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
public class ServiceNowEntryDTO {

    @JsonProperty("display_value")
    private String displayValue;

    @JsonProperty("short_description")
    private String shortDescription;

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
        private List<ServiceNowEntryJournalDTO> journal;

        @JsonProperty("changes")
        private List<ServiceNowEntryJournalDTO> changes;
    }
}
