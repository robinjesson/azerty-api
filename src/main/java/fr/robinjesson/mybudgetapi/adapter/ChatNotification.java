package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public class ChatNotification {
    private final Sinks.Many<ServerSentEvent<MessageResponse>> globalSink = Sinks.many()
            .multicast()
            .directBestEffort();

    public Flux<ServerSentEvent<MessageResponse>> getMessageStream(final Long conversationId) {
        return globalSink.asFlux()
                .filter(event ->
                        conversationId.toString().equals(event.comment())
                )
                .startWith(
                        ServerSentEvent.<MessageResponse>builder()
                                .event("connected")
                                .comment(conversationId.toString())
                                .build()
                );
    }

    public void publish(final MessageResponse messageResponse) {
        final ServerSentEvent<MessageResponse> event = ServerSentEvent.<MessageResponse>builder()
                .event("message")
                .data(messageResponse)
                .comment(messageResponse.conversation().id().toString())
                .id(UUID.randomUUID().toString())
                .build();

        globalSink.tryEmitNext(event);
    }
}
