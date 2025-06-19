package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.LoginRequest;
import fr.robinjesson.mybudgetapi.api.request.RegisterUserRequest;
import fr.robinjesson.mybudgetapi.api.response.LoginResponse;
import fr.robinjesson.mybudgetapi.api.response.UserResponse;
import fr.robinjesson.mybudgetapi.businesses.JwtBusiness;
import fr.robinjesson.mybudgetapi.businesses.UserBusiness;
import fr.robinjesson.mybudgetapi.entities.UserEntity;
import fr.robinjesson.mybudgetapi.mappers.UserMapper;
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
