package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.ConversationAdapter;
import fr.robinjesson.mybudgetapi.api.request.ConversationRequest;
import fr.robinjesson.mybudgetapi.api.request.MessageRequest;
import fr.robinjesson.mybudgetapi.api.response.ConversationResponse;
import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationAdapter conversationAdapter;

    @PostMapping
    public ResponseEntity<ConversationResponse> create(@RequestBody final ConversationRequest conversationRequest) {
        return ResponseEntity.status(201).body(conversationAdapter.create(conversationRequest));
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> findForConnectedUser() {
        return ResponseEntity.ok(conversationAdapter.findForConnectedUser());
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> findMessagesByConversationId(@PathVariable final Long conversationId) {
        return ResponseEntity.ok(conversationAdapter.findMessagesByConversationId(conversationId));
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageResponse> createMessage(@PathVariable final Long conversationId, @RequestBody final MessageRequest messageRequest) {
        return ResponseEntity.ok(conversationAdapter.createMessage(conversationId, messageRequest));
    }

    @Operation(hidden = true)
    @GetMapping(value = "/{conversationId}/open", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MessageResponse>> openConnection(@PathVariable Long conversationId) {
        return conversationAdapter.getMessageStream(conversationId);
    }
}
