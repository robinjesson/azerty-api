package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.ConversationAdapter;
import fr.robinjesson.mybudgetapi.api.request.MessageRequest;
import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationAdapter conversationAdapter;

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> findMessagesByConversationId(@PathVariable final Long conversationId) {
        return ResponseEntity.ok(conversationAdapter.findMessagesByConversationId(conversationId));
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageResponse> createMessage(@PathVariable final Long conversationId, @RequestBody final MessageRequest messageRequest) {
        return ResponseEntity.ok(conversationAdapter.createMessage(conversationId, messageRequest));
    }
}
