package com.thoughtworks.otr.snconnector.utils.mapper;

import com.thoughtworks.otr.snconnector.dto.TrelloConfigDTO;
import com.thoughtworks.otr.snconnector.entity.TrelloConfig;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TrelloConfigMapper {
    TrelloConfigMapper TRELLO_CONFIG_MAPPER = Mappers.getMapper(TrelloConfigMapper.class);

    TrelloConfigDTO toTrelloConfigDTO(TrelloConfig trelloConfig);
    TrelloConfig toTrelloConfig(TrelloConfigDTO trelloConfigDTO);
}
