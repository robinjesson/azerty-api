package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.ConversationRequest;
import fr.robinjesson.mybudgetapi.api.request.MessageRequest;
import fr.robinjesson.mybudgetapi.api.response.ConversationResponse;
import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import fr.robinjesson.mybudgetapi.businesses.ConversationBusiness;
import fr.robinjesson.mybudgetapi.mappers.ConversationMapper;
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
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final ChatNotification chatNotification;

    public ConversationResponse create(final ConversationRequest request) {
        return conversationMapper.mapToResponse(conversationBusiness.create(request.participantUids()));
    }

    public List<ConversationResponse> findForConnectedUser() {
        return conversationMapper.mapToResponse(conversationBusiness.findForConnectedUser());
    }

    public List<MessageResponse> findMessagesByConversationId(final Long conversationId) {
        return messageMapper.mapToResponse(conversationBusiness.findMessagesByConversationId(conversationId));
    }

    public Flux<ServerSentEvent<MessageResponse>> getMessageStream(final Long conversationId) {
        return chatNotification.getMessageStream(conversationId);
    }

    public MessageResponse createMessage(final Long conversationId, final MessageRequest messageRequest) {
        final MessageResponse response = messageMapper.mapToResponse(conversationBusiness.createMessageForUser(conversationId, messageRequest.text()));
        chatNotification.publish(response);
        return response;
    }
}
