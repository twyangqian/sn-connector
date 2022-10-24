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
public class TrelloCardComment {

    public static final String COMMENT_TYPE = "活动类型";
    public static final String COMMENT_DATE = "活动时间";
    public static final String COMMENT_FORMAT_STRING = "%s: %s\n%s: %s\n%s: %s";

    private String commentText;
    private String commentType;
    private String createdBy;
    private String createdDate;

    public String buildTrelloCardCommentText() {
        return String.format(
                COMMENT_FORMAT_STRING, COMMENT_TYPE,
                commentType, COMMENT_DATE,
                createdDate, createdBy, commentText);
    }
}
