package com.thoughtworks.otr.snconnector.service;

import com.thoughtworks.otr.snconnector.dto.TrelloConfigDTO;
import com.thoughtworks.otr.snconnector.entity.TrelloConfig;
import com.thoughtworks.otr.snconnector.enums.Squad;
import com.thoughtworks.otr.snconnector.repository.TrelloConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.thoughtworks.otr.snconnector.utils.mapper.TrelloConfigMapper.TRELLO_CONFIG_MAPPER;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrelloConfigService {

    private final TrelloConfigRepository repository;

    public TrelloConfigDTO createOrUpdateTrelloConfig(TrelloConfigDTO trelloConfigDTO) {
        Optional<TrelloConfig> trelloConfig = repository.findBySquad(trelloConfigDTO.getSquad());
        if (trelloConfig.isPresent()) {
            trelloConfig.get().setTrelloBoardId(trelloConfigDTO.getTrelloBoardId());
            trelloConfig.get().setDefaultListCardName(trelloConfigDTO.getDefaultListCardName());
            trelloConfig.get().setTrelloConfigCheckLists(trelloConfigDTO.getTrelloConfigCheckLists());
            return TRELLO_CONFIG_MAPPER.toTrelloConfigDTO(repository.save(trelloConfig.get()));
        }
        return TRELLO_CONFIG_MAPPER.toTrelloConfigDTO(
                repository.save(TRELLO_CONFIG_MAPPER.toTrelloConfig(trelloConfigDTO)));
    }

    public TrelloConfigDTO getTrelloConfigBySquad(Squad squad) {
        return repository.findBySquad(squad)
                         .map(TRELLO_CONFIG_MAPPER::toTrelloConfigDTO)
                         .orElse(TrelloConfigDTO.builder()
                                                .squad(squad)
                                                .build());
    }
}
