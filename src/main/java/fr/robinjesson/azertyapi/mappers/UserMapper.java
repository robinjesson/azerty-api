package fr.robinjesson.azertyapi.mappers;

import fr.robinjesson.azertyapi.api.request.LoginRequest;
import fr.robinjesson.azertyapi.api.request.RegisterUserRequest;
import fr.robinjesson.azertyapi.api.response.UserResponse;
import fr.robinjesson.azertyapi.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper {
    UserEntity mapToEntity(RegisterUserRequest source);
    UserEntity mapToEntity(LoginRequest source);
    UserResponse mapToResponse(UserEntity source);
}
