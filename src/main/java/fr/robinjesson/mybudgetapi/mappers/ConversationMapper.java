package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.response.ConversationResponse;
import fr.robinjesson.mybudgetapi.entities.ConversationEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface ConversationMapper {
    ConversationResponse mapToResponse(ConversationEntity entity);
    List<ConversationResponse> mapToResponse(List<ConversationEntity> entities);
}
