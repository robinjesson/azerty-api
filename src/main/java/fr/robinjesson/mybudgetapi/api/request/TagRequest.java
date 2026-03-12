package fr.robinjesson.mybudgetapi.api.request;

import fr.robinjesson.mybudgetapi.entities.enums.Category;
import jakarta.validation.constraints.NotBlank;

public record TagRequest(
        @NotBlank String label,
        Category category
) { }
