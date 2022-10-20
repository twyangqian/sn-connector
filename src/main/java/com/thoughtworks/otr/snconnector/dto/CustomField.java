package com.thoughtworks.otr.snconnector.dto;

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
public class CustomField {
    private String id;
    private String idModel;
    private String modelType;
    private String fieldGroup;
    private String name;
    private String pos;
    private String type;
    private Object[] options;
    private Object display;
}
