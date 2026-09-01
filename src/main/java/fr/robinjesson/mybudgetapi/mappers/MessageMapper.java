package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import fr.robinjesson.mybudgetapi.entities.MessageEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapperConfiguration.class, uses = {
        UserMapper.class,
        ConversationMapper.class
})
public interface MessageMapper {
    MessageResponse mapToResponse(MessageEntity entity);

    List<MessageResponse> mapToResponse(List<MessageEntity> entities);
}
