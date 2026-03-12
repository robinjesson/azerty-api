package fr.robinjesson.mybudgetapi.api.request;

import fr.robinjesson.mybudgetapi.entities.enums.TransactionTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;

public record TransactionRequest(
        @NotNull BigDecimal amount,
        @NotNull TransactionTypeEnum transactionType,
        @NotEmpty Set<String> tagLabels
) { }


