package fr.robinjesson.azertyapi.security;

import fr.robinjesson.azertyapi.entities.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
@Getter
@Setter
public class AzertyUser extends UserEntity {
    private UUID uuid;
    private String email;
}
