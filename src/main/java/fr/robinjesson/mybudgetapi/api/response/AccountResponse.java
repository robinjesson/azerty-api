package fr.robinjesson.mybudgetapi.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(
        Long id,
        String name,
        BigDecimal startAmount,
        LocalDate pointingDate
) { }
