package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.AuthAdapter;
import fr.robinjesson.mybudgetapi.adapter.UserAdapter;
import fr.robinjesson.mybudgetapi.api.request.LoginRequest;
import fr.robinjesson.mybudgetapi.api.request.RegisterUserRequest;
import fr.robinjesson.mybudgetapi.api.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and authenticate users")
public class AuthController {

    private final UserAdapter userAdapter;
    private final AuthAdapter authAdapter;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "403", description = "User already exists")
    })
    public ResponseEntity<EntityModel<UserResponse>> signup(@RequestBody final RegisterUserRequest registerUserRequest) {
        final UserResponse userResponse = userAdapter.signup(registerUserRequest);
        return new ResponseEntity<>(
                EntityModel.of(userResponse,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getMe()).withRel("me")),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user and return a JWT token in an HTTP-only cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Authentication successful, JWT set in cookie"),
            @ApiResponse(responseCode = "403", description = "Invalid credentials")
    })
    public ResponseEntity<Void> login(@RequestBody final LoginRequest loginRequest) {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authAdapter.authenticate(loginRequest).toString())
                .build();
    }
}
