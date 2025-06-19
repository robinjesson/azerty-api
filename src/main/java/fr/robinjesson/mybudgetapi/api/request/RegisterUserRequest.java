package fr.robinjesson.mybudgetapi.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {
    private String uid;
    private String email;
    private String password;
}
