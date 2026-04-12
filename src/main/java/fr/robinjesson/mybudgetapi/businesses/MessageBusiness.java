package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.MessageEntity;
import fr.robinjesson.mybudgetapi.exception.NotFoundException;
import fr.robinjesson.mybudgetapi.repository.ConversationRepository;
import fr.robinjesson.mybudgetapi.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageBusiness {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public List<MessageEntity> findMessagesByConversationId(final Long conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new NotFoundException("Conversation not found");
        }
        return messageRepository.findByConversationId(conversationId);
    }
}
