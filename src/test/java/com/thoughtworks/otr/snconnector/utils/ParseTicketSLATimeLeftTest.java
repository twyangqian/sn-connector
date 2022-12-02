package com.thoughtworks.otr.snconnector.utils;

import com.thoughtworks.otr.snconnector.client.impl.TrelloBoardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloCardClientImpl;
import com.thoughtworks.otr.snconnector.repository.ServiceNowSyncDataRepository;
import com.thoughtworks.otr.snconnector.repository.TrelloConfigRepository;
import com.thoughtworks.otr.snconnector.service.TrelloService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ParseTicketSLATimeLeftTest {

    @Mock
    TrelloBoardClientImpl trelloBoardClient;
    @Mock
    TrelloCardClientImpl trelloCardClient;
    @Mock
    TrelloConfigRepository trelloConfigRepository;
    @Mock
    ServiceNowSyncDataRepository syncDataRepository;

    @InjectMocks
    TrelloService trelloService = new TrelloService(trelloBoardClient, trelloCardClient, trelloConfigRepository, syncDataRepository);

    @Test
    void parse_tests() {
        assertEquals(Duration.of(1, ChronoUnit.DAYS).plusHours(6).plusMinutes(20),
                trelloService.parseTicketSLATimeLeft(" 1 Day 6 Hours 20 Minutes"));

        assertEquals(Duration.of(1, ChronoUnit.DAYS),
                trelloService.parseTicketSLATimeLeft(" 1 Day "));

        assertEquals(Duration.of(1, ChronoUnit.DAYS).plusHours(6),
                trelloService.parseTicketSLATimeLeft(" 1 Day 6 Hours "));

        assertEquals(Duration.of(1, ChronoUnit.DAYS).plusMinutes(20),
                trelloService.parseTicketSLATimeLeft(" 1 Day 20 Minutes"));

        assertEquals(Duration.of(6, ChronoUnit.HOURS).plusMinutes(20),
                trelloService.parseTicketSLATimeLeft(" 6 Hours 20 Minutes"));

        assertEquals(Duration.of(6, ChronoUnit.HOURS),
                trelloService.parseTicketSLATimeLeft(" 6 Hours "));

        assertEquals(Duration.of(20, ChronoUnit.MINUTES),
                trelloService.parseTicketSLATimeLeft(" 20 Minutes"));
    }
}
