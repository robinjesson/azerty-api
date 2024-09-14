package fr.robinjesson.azertyapi.api;

import fr.robinjesson.azertyapi.security.AzertyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AzertyUser azertyUser;

    @GetMapping("/me")
    public String getUserById() {
        return "User " + azertyUser.getEmail();
    }

}
