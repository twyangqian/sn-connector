package com.thoughtworks.otr.snconnector.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CustomFieldItemType {
    NUMBER,
    DATE,
    TEXT,
    CHECKBOX,
    LIST;

    @JsonCreator
    public static CustomFieldItemType getFieldType(String fieldType) {
        return Arrays.stream(CustomFieldItemType.values())
                     .filter(type -> type.name().equalsIgnoreCase(fieldType))
                     .findFirst()
                     .orElseThrow(() -> new TrelloException("error field type: " + fieldType));
    }

    @JsonValue
    public String SerializationFiledType() {
        return this.name().toLowerCase();
    }
}
