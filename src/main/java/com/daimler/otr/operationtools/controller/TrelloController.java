package com.daimler.otr.operationtools.controller;

import com.daimler.otr.operationtools.dto.CreateTrelloCardDTO;
import com.daimler.otr.operationtools.dto.TrelloCard;
import com.daimler.otr.operationtools.service.TrelloService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/api/operation/tools")
@Slf4j
public class TrelloController {

    private final TrelloService trelloService;

    @PostMapping("/trello-cards")
    public ResponseEntity<TrelloCard> createTrelloCard(@RequestBody CreateTrelloCardDTO createTrelloCardDTO) {
        return ResponseEntity.ok(trelloService.createTrelloCard(createTrelloCardDTO));
    }
}
