package fr.robinjesson.mybudgetapi.api.dto;

public record SimulationSendRequest(
        String chatId,
        String content
) {}
