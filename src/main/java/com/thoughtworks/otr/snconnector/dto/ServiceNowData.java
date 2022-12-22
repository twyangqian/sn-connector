package com.thoughtworks.otr.snconnector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceNowData {

    @NotBlank
    private String ticketFullDescription;

    @NotBlank
    private String contactUserD8Account;

    @NotNull
    private ServiceNowSLA sla;

    @NotNull
    private List<ServiceNowDataEntry> entries;

    private List<ServiceNowDataFile> files;
}
