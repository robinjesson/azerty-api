package fr.robinjesson.azertyapi.adapter;

import fr.robinjesson.azertyapi.api.request.LoginRequest;
import fr.robinjesson.azertyapi.api.request.RegisterUserRequest;
import fr.robinjesson.azertyapi.api.response.LoginResponse;
import fr.robinjesson.azertyapi.api.response.UserResponse;
import fr.robinjesson.azertyapi.businesses.JwtBusiness;
import fr.robinjesson.azertyapi.businesses.UserBusiness;
import fr.robinjesson.azertyapi.entities.UserEntity;
import fr.robinjesson.azertyapi.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdapter {
    private final UserMapper userMapper;
    private final UserBusiness userBusiness;
    private final JwtBusiness jwtBusiness;

    public UserResponse signup(final RegisterUserRequest registerUserRequest) {
        UserEntity userEntity = userMapper.mapToEntity(registerUserRequest);
        userEntity = userBusiness.create(userEntity);
        return userMapper.mapToResponse(userEntity);
    }

    public LoginResponse authenticate(final LoginRequest loginRequest) {
        final UserEntity user = userBusiness.authenticate(
                userMapper.mapToEntity(loginRequest)
        );
        return LoginResponse.builder()
                .token(jwtBusiness.generateToken(user))
                .expiresIn(jwtBusiness.getExpirationTime())
                .build();
    }
}
