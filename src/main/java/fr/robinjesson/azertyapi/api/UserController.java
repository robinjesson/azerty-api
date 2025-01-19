package fr.robinjesson.azertyapi.api;

import fr.robinjesson.azertyapi.api.response.UserResponse;
import fr.robinjesson.azertyapi.security.AzertyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AzertyUser azertyUser;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserById() {
        final UserResponse userResponse = new UserResponse();
        userResponse.setUid(azertyUser.getUid());
        userResponse.setEmail(azertyUser.getEmail());
        return ResponseEntity.ok(userResponse);
    }

}
