package fr.robinjesson.mybudgetapi.api.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class TransactionResponse {
    private UUID uuid;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private Boolean isPointed;
    private Boolean isReconciled;
    private Set<TagResponse> tags;
}

