package com.thoughtworks.otr.snconnector.dto;

import com.thoughtworks.otr.snconnector.entity.TrelloConfigCheckList;
import com.thoughtworks.otr.snconnector.enums.Squad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TrelloConfigDTO {
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private Squad squad;

    @NotBlank
    private String trelloBoardId;

    @NotBlank
    private String defaultListCardName;

    @Builder.Default
    private List<TrelloConfigCheckList> trelloConfigCheckLists = new ArrayList<>();
}
