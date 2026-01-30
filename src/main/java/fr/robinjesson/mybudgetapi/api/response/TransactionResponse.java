package fr.robinjesson.mybudgetapi.api.response;

import fr.robinjesson.mybudgetapi.entities.enums.TransactionTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private TransactionTypeEnum transactionType;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private Boolean isPointed;
    private Boolean isReconciled;
    private Set<TagResponse> tags;
}

