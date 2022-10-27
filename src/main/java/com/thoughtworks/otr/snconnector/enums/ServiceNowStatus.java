package com.thoughtworks.otr.snconnector.enums;

import com.thoughtworks.otr.snconnector.exception.TrelloException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ServiceNowStatus {
    NEW("New", "创建"),
    OPEN("Open", "进行中"),
    AWAITING_INFO("Awaiting Info", "等待信息"),
    RESOLVED("Resolved", "已解决"),
    CLOSED("Closed", "已结案"),
    CANCELLED("Cancelled", "已取消");

    private final String value;
    private final String description;

    ServiceNowStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ServiceNowStatus getServiceNowStatus(String value) {
        return Arrays.stream(ServiceNowStatus.values())
              .filter(serviceNowStatus -> serviceNowStatus.getValue().equalsIgnoreCase(value))
              .findFirst()
              .orElseThrow(() -> new TrelloException("error service now status: " + value));
    }
}
