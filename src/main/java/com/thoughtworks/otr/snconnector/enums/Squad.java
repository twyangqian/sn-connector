package com.thoughtworks.otr.snconnector.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Squad {
    PARTS,
    RWO,
    SALES,
    ACCOUNTING,
    WARRANTY,
    WORKSHOP,
    OPERATION,
    ACCIDENT;

    @JsonCreator
    public static Squad getSquad(String squad) {
        return Arrays.stream(Squad.values())
                .filter(squadName -> squadName.name().equalsIgnoreCase(squad))
                .findFirst()
                .orElseThrow(() -> new TrelloException("error squad name: " + squad));
    }
}
