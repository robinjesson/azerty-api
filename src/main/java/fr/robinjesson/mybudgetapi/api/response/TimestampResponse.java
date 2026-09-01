package fr.robinjesson.mybudgetapi.api.response;

import java.time.LocalDateTime;

public record TimestampResponse(
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
