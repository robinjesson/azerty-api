package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import fr.robinjesson.mybudgetapi.entities.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface MessageMapper {
    @Mapping(source = "user.uid", target = "userUid")
    MessageResponse mapToResponse(MessageEntity entity);

    List<MessageResponse> mapToResponse(List<MessageEntity> entities);
}
