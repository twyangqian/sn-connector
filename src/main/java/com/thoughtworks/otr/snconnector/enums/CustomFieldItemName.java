package com.thoughtworks.otr.snconnector.enums;

import lombok.Getter;

@Getter
public enum CustomFieldItemName {
    TICKET("ticket"),
    PRIORITY("Priority"),
    STATUS("Status"),
    CONTACT("Contact"),
    ASSIGNED_TO("Assigned to"),
    TICKET_OPEN_DATE("Ticket open date");


    final String value;

    CustomFieldItemName(String value) {
        this.value = value;
    }
}
