package fr.robinjesson.mybudgetapi.api.response;

public record MessageResponse(
        Long id,
        String text,
        UserResponse user,
        ConversationResponse conversation
) { }
