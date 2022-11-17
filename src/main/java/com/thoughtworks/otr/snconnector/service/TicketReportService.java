package com.thoughtworks.otr.snconnector.service;

import com.julienvey.trello.domain.TList;
import com.thoughtworks.otr.snconnector.client.impl.TrelloBoardClientImpl;
import com.thoughtworks.otr.snconnector.client.impl.TrelloListCardClientImpl;
import com.thoughtworks.otr.snconnector.dto.report.PartsTicketReportDTO;
import com.thoughtworks.otr.snconnector.entity.ServiceNowSyncData;
import com.thoughtworks.otr.snconnector.entity.TrelloConfig;
import com.thoughtworks.otr.snconnector.enums.Squad;
import com.thoughtworks.otr.snconnector.exception.TrelloException;
import com.thoughtworks.otr.snconnector.repository.ServiceNowSyncDataRepository;
import com.thoughtworks.otr.snconnector.repository.TrelloConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.thoughtworks.otr.snconnector.utils.mapper.ServiceNowSyncDataMapper.SERVICE_NOW_SYNC_DATA_MAPPER;

@Service
@RequiredArgsConstructor
public class TicketReportService {

    private final TrelloConfigRepository trelloConfigRepository;
    private final TrelloBoardClientImpl trelloBoardClient;
    private final TrelloListCardClientImpl trelloListCardClient;
    private final ServiceNowSyncDataRepository syncDataRepository;


    public List<PartsTicketReportDTO> getPartsOpenTicketReport() {
        TrelloConfig trelloConfig =
                trelloConfigRepository.findBySquad(Squad.PARTS)
                                      .orElseThrow(
                                              () -> new TrelloException("can not found trello config by parts squad"));
        TList trelloListCard = trelloBoardClient.getBoardListCards(trelloConfig.getTrelloBoardId())
                                       .stream()
                                       .filter(listCard -> listCard.getName().equalsIgnoreCase(trelloConfig.getDefaultListCardName()))
                                       .findFirst()
                                       .orElseThrow(
                                               () -> new TrelloException("can not found trello list card")
                                       );
        return trelloListCardClient.getListCardCards(trelloListCard.getId())
                .stream()
                .map(card -> {
                    ServiceNowSyncData serviceNowSyncData =
                            syncDataRepository.findByTrelloCardId(card.getId())
                                              .orElseThrow(() -> new TrelloException(
                                                              String.format("can not found service now sync data by trello card %s, %s",
                                                                      card.getName(), card.getId())));
                    return SERVICE_NOW_SYNC_DATA_MAPPER.toPartTicketReportDTO(serviceNowSyncData);
                })
                .collect(Collectors.toList());
    }
}
