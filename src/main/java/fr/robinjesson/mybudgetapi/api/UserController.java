package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.api.response.UserResponse;
import fr.robinjesson.mybudgetapi.security.ConnectedUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final ConnectedUser connectedUser;

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user's information")
    public ResponseEntity<UserResponse> getUserById() {
        final UserResponse userResponse = new UserResponse(
                connectedUser.getUid(),
                connectedUser.getEmail()
        );
        return ResponseEntity.ok(userResponse);
    }

}
