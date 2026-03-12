package fr.robinjesson.mybudgetapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountCreationRequest(
        @NotBlank String name,
        @NotNull BigDecimal startAmount
) { }
