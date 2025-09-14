package fr.robinjesson.mybudgetapi.api.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountCreationRequest {
    private String name;
    private BigDecimal startAmount;
}
