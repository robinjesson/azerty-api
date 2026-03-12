package fr.robinjesson.mybudgetapi.api.request;

public record LoginRequest(
        String uid,
        String password
) { }
