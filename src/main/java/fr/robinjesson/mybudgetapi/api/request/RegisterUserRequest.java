package fr.robinjesson.mybudgetapi.api.request;

public record RegisterUserRequest(
        String uid,
        String email,
        String password
) { }
