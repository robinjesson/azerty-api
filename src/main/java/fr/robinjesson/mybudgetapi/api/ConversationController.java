package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.MessageAdapter;
import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final MessageAdapter messageAdapter;

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> findMessagesByConversationId(@PathVariable final Long conversationId) {
        return ResponseEntity.ok(messageAdapter.findMessagesByConversationId(conversationId));
    }
}
