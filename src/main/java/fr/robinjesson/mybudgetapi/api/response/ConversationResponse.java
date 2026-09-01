package fr.robinjesson.mybudgetapi.api.response;

import java.util.List;

public record ConversationResponse(
        Long id,
        List<UserResponse> participants
) { }
