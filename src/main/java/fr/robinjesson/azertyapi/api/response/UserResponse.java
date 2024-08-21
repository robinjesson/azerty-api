package fr.robinjesson.azertyapi.api.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID uuid;
    private String email;
}
