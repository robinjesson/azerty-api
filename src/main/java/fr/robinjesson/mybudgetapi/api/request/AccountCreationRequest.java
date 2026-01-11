package fr.robinjesson.mybudgetapi.api.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class AccountCreationRequest {
    @NotBlank
    private String name;
    @NotNull
    private BigDecimal startAmount;
}
