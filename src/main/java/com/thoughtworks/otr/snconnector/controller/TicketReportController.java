package com.thoughtworks.otr.snconnector.controller;


import com.thoughtworks.otr.snconnector.dto.report.PartsTicketReportDTO;
import com.thoughtworks.otr.snconnector.service.TicketReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/sn-connector/report")
@Slf4j
public class TicketReportController {

    private final TicketReportService ticketReportService;


    @GetMapping("/parts/open-tickets")
    public List<PartsTicketReportDTO> getPartsOpenTicketReport() {
        return ticketReportService.getPartsOpenTicketReport();
    }
}
