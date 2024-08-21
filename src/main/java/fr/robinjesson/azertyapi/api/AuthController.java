package fr.robinjesson.azertyapi.api;

import fr.robinjesson.azertyapi.adapter.UserAdapter;
import fr.robinjesson.azertyapi.api.request.LoginRequest;
import fr.robinjesson.azertyapi.api.request.RegisterUserRequest;
import fr.robinjesson.azertyapi.api.response.LoginResponse;
import fr.robinjesson.azertyapi.api.response.UserResponse;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody final RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(userAdapter.signup(registerUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest loginRequest) {
        return ResponseEntity.ok(userAdapter.authenticate(loginRequest));
    }
}
