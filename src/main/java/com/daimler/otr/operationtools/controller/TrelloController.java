package com.daimler.otr.operationtools.controller;

import com.daimler.otr.operationtools.dto.CreateTrelloCardDTO;
import com.daimler.otr.operationtools.dto.TrelloCard;
import com.daimler.otr.operationtools.exception.TrelloException;
import com.daimler.otr.operationtools.service.TrelloService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public TrelloCard createTrelloCard(@RequestBody CreateTrelloCardDTO createTrelloCardDTO) {
        try {
            return trelloService.createTrelloCard(createTrelloCardDTO);
        } catch (Exception e) {
            log.info("create trello card failed, error message: {}", e.getMessage());
            throw new TrelloException("create trello card failed!", e.getCause());
        }

    }
}
