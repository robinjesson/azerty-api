package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.response.MessageResponse;
import fr.robinjesson.mybudgetapi.businesses.MessageBusiness;
import fr.robinjesson.mybudgetapi.mappers.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageAdapter {
    private final MessageBusiness messageBusiness;
    private final MessageMapper messageMapper;


    public List<MessageResponse> findMessagesByConversationId(final Long conversationId) {
        return messageMapper.mapToResponse(messageBusiness.findMessagesByConversationId(conversationId));
    }
}
