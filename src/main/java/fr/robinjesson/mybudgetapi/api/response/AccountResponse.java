package fr.robinjesson.mybudgetapi.api.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AccountResponse {
    private UUID uuid;
    private String name;
    private BigDecimal startAmount;
    private LocalDate pointingDate;
}
