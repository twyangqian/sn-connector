package com.thoughtworks.otr.snconnector.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResult {
    private Boolean success;

    private String errorMessage;
}
