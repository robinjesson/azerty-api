package fr.robinjesson.azertyapi.api;

import fr.robinjesson.azertyapi.api.response.UserResponse;
import fr.robinjesson.azertyapi.security.ConnectedUser;
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
    public ResponseEntity<UserResponse> getUserById() {
        final UserResponse userResponse = new UserResponse();
        userResponse.setUid(connectedUser.getUid());
        userResponse.setEmail(connectedUser.getEmail());
        return ResponseEntity.ok(userResponse);
    }

}
