package fr.robinjesson.mybudgetapi.api.request;

import java.util.List;

public record ConversationRequest(
        List<String> participantUids
) {
}
