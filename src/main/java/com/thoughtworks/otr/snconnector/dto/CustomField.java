package com.thoughtworks.otr.snconnector.dto;

import com.thoughtworks.otr.snconnector.enums.CustomFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private CustomFieldType type;
    private CustomFieldDisplay display;

    @Getter
    @Setter
    @Builder
    public static class CustomFieldDisplay {
        private boolean cardFront;
        private String name;
        private String pos;
        private List<DisplayOption> options;

        @Getter
        @Setter
        @Builder
        public static class DisplayOption {
            private String id;
            private String idCustomField;
            private Object value;
            private String color;
            private String pos;
        }
    }
}
