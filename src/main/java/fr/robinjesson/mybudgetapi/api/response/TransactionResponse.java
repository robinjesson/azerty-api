package fr.robinjesson.mybudgetapi.api.response;

import fr.robinjesson.mybudgetapi.entities.enums.TransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record TransactionResponse(
        Long id,
        TransactionTypeEnum transactionType,
        BigDecimal amount,
        LocalDate transactionDate,
        Boolean isPointed,
        Boolean isReconciled,
        Set<TagResponse> tags,
        AccountResponse account
) { }

