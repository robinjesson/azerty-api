package fr.robinjesson.mybudgetapi.api.dto;

public record SimulationSendRequest(
        String userId,
        String content
) {}
