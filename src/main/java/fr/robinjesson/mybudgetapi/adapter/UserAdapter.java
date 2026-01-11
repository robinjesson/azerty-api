package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.RegisterUserRequest;
import fr.robinjesson.mybudgetapi.api.response.UserResponse;
import fr.robinjesson.mybudgetapi.businesses.UserBusiness;
import fr.robinjesson.mybudgetapi.entities.UserEntity;
import fr.robinjesson.mybudgetapi.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAdapter {
    private final UserMapper userMapper;
    private final UserBusiness userBusiness;

    public UserResponse signup(final RegisterUserRequest registerUserRequest) {
        UserEntity userEntity = userMapper.mapToEntity(registerUserRequest);
        userEntity.setLastPasswordModification(LocalDateTime.now());
        userEntity = userBusiness.create(userEntity);
        return userMapper.mapToResponse(userEntity);
    }
}
