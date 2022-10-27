package com.thoughtworks.otr.snconnector.dto;

import com.thoughtworks.otr.snconnector.enums.ServiceNowStatus;
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
public class ServiceNowDataStatusChange {

    private ServiceNowStatus status;

    private String statusChangeData;
}
