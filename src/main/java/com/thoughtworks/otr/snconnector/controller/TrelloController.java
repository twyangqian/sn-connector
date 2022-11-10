package com.thoughtworks.otr.snconnector.controller;

import com.thoughtworks.otr.snconnector.dto.ServiceNowData;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.service.TrelloService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;

@Controller
@AllArgsConstructor
@RequestMapping("/api/sn-connector/trello")
@Slf4j
public class TrelloController {

    private final TrelloService trelloService;

    @PostMapping("/cards")
    public ResponseEntity<TrelloCard> createTrelloCard(
            @RequestParam(name = "boardId") @NotBlank String boardId,
            @RequestParam(name = "defaultListCard", defaultValue = "TODO") String defaultListCard,
            @RequestBody ServiceNowData serviceNowData) {
        return ResponseEntity.ok(trelloService.createTrelloCard(boardId, defaultListCard, serviceNowData));
    }
}
