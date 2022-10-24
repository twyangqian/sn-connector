package com.thoughtworks.otr.snconnector.utils.mapper;

import com.julienvey.trello.domain.Card;
import com.thoughtworks.otr.snconnector.dto.TrelloCard;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TrelloCardMapper {

    TrelloCardMapper TRELLO_CARD_MAPPER = Mappers.getMapper(TrelloCardMapper.class);

    Card toCard(TrelloCard trelloCard);

    TrelloCard toTrelloCard(Card card);
}
