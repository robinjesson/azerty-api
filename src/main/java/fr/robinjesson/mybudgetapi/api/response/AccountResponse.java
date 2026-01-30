package fr.robinjesson.mybudgetapi.api.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AccountResponse {
    private Long id;
    private String name;
    private BigDecimal startAmount;
    private LocalDate pointingDate;
}
