package fr.robinjesson.mybudgetapi.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Consts {
    public static final String COOKIE_NAME = "jwt-token";
}
