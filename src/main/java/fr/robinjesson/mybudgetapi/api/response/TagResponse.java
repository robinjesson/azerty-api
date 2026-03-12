package fr.robinjesson.mybudgetapi.api.response;

import fr.robinjesson.mybudgetapi.entities.enums.Category;

public record TagResponse(
        String label,
        Category category
) { }
