package com.thoughtworks.otr.snconnector.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ServiceNowEntryFieldName {
    COMMENTS("补充备注", "comments"),
    OPENED_BY("提交人", ""),
    IMPACT("影响", ""),
    STATE("状态", CustomFieldItemName.STATUS.value),
    PRIORITY("优先级", CustomFieldItemName.PRIORITY.value),
    WORK_NOTES("工作注解", "workComments"),
    ASSIGNED_TO("已指派给", CustomFieldItemName.ASSIGNED_TO.value);

    final String label;

    final String trelloFieldName;

    ServiceNowEntryFieldName(String label, String trelloFieldName) {
        this.label = label;
        this.trelloFieldName = trelloFieldName;
    }

    @JsonCreator
    public static ServiceNowEntryFieldName getFieldName(String fieldName) {
        return Arrays.stream(ServiceNowEntryFieldName.values())
                .filter(serviceNowEntryFieldName -> serviceNowEntryFieldName.name().equalsIgnoreCase(fieldName))
                .findFirst()
                .orElseThrow(() -> new TrelloException("error field name: " + fieldName));
    }
}
