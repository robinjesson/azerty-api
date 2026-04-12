package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.MessageRequest;
import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import fr.robinjesson.mybudgetapi.businesses.ConversationBusiness;
import fr.robinjesson.mybudgetapi.entities.MessageEntity;
import fr.robinjesson.mybudgetapi.mappers.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationAdapter {
    private final ConversationBusiness conversationBusiness;
    private final MessageMapper messageMapper;

    public List<MessageResponse> findMessagesByConversationId(final Long conversationId) {
        return messageMapper.mapToResponse(conversationBusiness.findMessagesByConversationId(conversationId));
    }

    public Flux<ServerSentEvent<MessageEntity>> getMessageStream(final Long conversationId) {
        return conversationBusiness.getMessageStream(conversationId);
    }

    public MessageResponse createMessage(final Long conversationId, final MessageRequest messageRequest) {
        return messageMapper.mapToResponse(conversationBusiness.createMessageForUser(conversationId, messageRequest.text()));
    }
}
