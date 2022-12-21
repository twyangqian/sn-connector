package com.thoughtworks.otr.snconnector.controller;

import com.thoughtworks.otr.snconnector.dto.jira.JiraRequest;
import com.thoughtworks.otr.snconnector.service.JiraService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/api/sn-connector/jira")
@Slf4j
public class JiraController {

    private final JiraService jiraService;

    @PostMapping
    public ResponseEntity<String> createJira(@RequestBody JiraRequest jiraRequest) {
        return ResponseEntity.ok(jiraService.createIssue(jiraRequest));
    }
}
