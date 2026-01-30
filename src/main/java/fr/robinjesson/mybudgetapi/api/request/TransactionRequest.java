package fr.robinjesson.mybudgetapi.api.request;

import fr.robinjesson.mybudgetapi.entities.enums.TransactionTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class TransactionRequest {
    @NotNull
    private BigDecimal amount;

    @NotNull
    private TransactionTypeEnum transactionType;

    @NotEmpty
    private Set<String> tagLabels;
}


