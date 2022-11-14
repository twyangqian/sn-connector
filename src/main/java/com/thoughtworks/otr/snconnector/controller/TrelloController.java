package com.thoughtworks.otr.snconnector.controller;

import com.thoughtworks.otr.snconnector.dto.ServiceNowData;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import com.thoughtworks.otr.snconnector.dto.TrelloConfigDTO;
import com.thoughtworks.otr.snconnector.enums.Squad;
import com.thoughtworks.otr.snconnector.service.TrelloConfigService;
import com.thoughtworks.otr.snconnector.service.TrelloService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/sn-connector/trello")
@Slf4j
public class TrelloController {

    private final TrelloService trelloService;

    private final TrelloConfigService trelloConfigService;

    @PostMapping("/cards")
    public ResponseEntity<TrelloCard> createTrelloCard(
            @RequestParam("squad") @NotBlank Squad squad,
            @RequestBody ServiceNowData serviceNowData) {
        return ResponseEntity.ok(trelloService.createTrelloCard(squad, serviceNowData));
    }

    @PostMapping("/config")
    public ResponseEntity<TrelloConfigDTO> createOrUpdateTrelloConfig(
            @RequestBody TrelloConfigDTO trelloConfigDTO) {
        return ResponseEntity.ok(trelloConfigService.createOrUpdateTrelloConfig(trelloConfigDTO));
    }

    @GetMapping("/config")
    public ResponseEntity<TrelloConfigDTO> getTrelloConfig(
            @RequestParam(name = "squad") @NotBlank Squad squad) {
        return ResponseEntity.ok(trelloConfigService.getTrelloConfigBySquad(squad));
    }
}
