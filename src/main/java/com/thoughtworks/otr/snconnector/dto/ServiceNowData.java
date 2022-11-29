package com.thoughtworks.otr.snconnector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
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

    @NotBlank
    private ServiceNowSLA sla;

    @NotBlank
    private List<ServiceNowDataEntry> entries;
}
