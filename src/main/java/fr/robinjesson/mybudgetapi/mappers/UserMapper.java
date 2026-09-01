package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.request.LoginRequest;
import fr.robinjesson.mybudgetapi.api.request.RegisterUserRequest;
import fr.robinjesson.mybudgetapi.api.response.UserResponse;
import fr.robinjesson.mybudgetapi.entities.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper {
    UserEntity mapToEntity(RegisterUserRequest source);
    UserEntity mapToEntity(LoginRequest source);
    UserResponse mapToResponse(UserEntity source);
    List<UserResponse> mapToResponse(List<UserEntity> source);
}
