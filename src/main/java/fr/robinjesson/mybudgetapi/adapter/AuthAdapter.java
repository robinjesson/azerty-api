package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.LoginRequest;
import fr.robinjesson.mybudgetapi.businesses.AuthBusiness;
import fr.robinjesson.mybudgetapi.businesses.JwtBusiness;
import fr.robinjesson.mybudgetapi.businesses.UserBusiness;
import fr.robinjesson.mybudgetapi.entities.UserEntity;
import fr.robinjesson.mybudgetapi.mappers.UserMapper;
import fr.robinjesson.mybudgetapi.security.Consts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthAdapter {
    private final UserMapper userMapper;
    private final AuthBusiness authBusiness;
    private final JwtBusiness jwtBusiness;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.cookie-secure}")
    private boolean cookieSecure;

    public ResponseCookie authenticate(final LoginRequest loginRequest) {
        final UserEntity user = authBusiness.authenticate(loginRequest.getUid(), loginRequest.getPassword());
        final String token = jwtBusiness.generateToken(user);
        return ResponseCookie.from(Consts.COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .secure(cookieSecure)
                .maxAge(jwtExpiration / 1000)
                .build();
    }
}
