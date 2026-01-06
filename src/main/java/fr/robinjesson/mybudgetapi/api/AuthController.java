package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.AuthAdapter;
import fr.robinjesson.mybudgetapi.adapter.UserAdapter;
import fr.robinjesson.mybudgetapi.api.request.LoginRequest;
import fr.robinjesson.mybudgetapi.api.request.RegisterUserRequest;
import fr.robinjesson.mybudgetapi.api.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAdapter userAdapter;
    private final AuthAdapter authAdapter;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody final RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(userAdapter.signup(registerUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody final LoginRequest loginRequest) {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authAdapter.authenticate(loginRequest).toString())
                .build();
    }
}
