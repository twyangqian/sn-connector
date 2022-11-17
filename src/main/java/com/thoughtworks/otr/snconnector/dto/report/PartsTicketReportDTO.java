package com.thoughtworks.otr.snconnector.dto.report;

import com.thoughtworks.otr.snconnector.enums.ServiceNowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartsTicketReportDTO {
    private String ticket;
    private String category;
    private String shortDescription;
    private String description;
    private String ticketOpenDate;
    private String owner;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ServiceNowStatus status = ServiceNowStatus.OPEN;
}
