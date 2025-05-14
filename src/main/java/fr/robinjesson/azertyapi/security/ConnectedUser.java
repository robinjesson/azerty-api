package fr.robinjesson.azertyapi.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class ConnectedUser {
    private String uid;
    private String email;
}
