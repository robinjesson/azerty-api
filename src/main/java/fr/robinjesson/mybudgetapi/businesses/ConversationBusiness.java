package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.ConversationEntity;
import fr.robinjesson.mybudgetapi.entities.MessageEntity;
import fr.robinjesson.mybudgetapi.exception.NotFoundException;
import fr.robinjesson.mybudgetapi.repository.ConversationRepository;
import fr.robinjesson.mybudgetapi.repository.MessageRepository;
import fr.robinjesson.mybudgetapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationBusiness {
    private final ConversationRepository conversationRepository;
    private final UserBusiness userBusiness;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    public List<MessageEntity> findMessagesByConversationId(final Long conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new NotFoundException("Conversation not found");
        }
        return messageRepository.findByConversationId(conversationId);
    }

    public ConversationEntity create(final List<String> participantUids) {
        final ConversationEntity conversation = new ConversationEntity();
        conversation.setParticipants(
                participantUids.stream()
                        .map(userRepository::findConcreteById)
                        .toList()
        );
        return conversationRepository.save(conversation);
    }

    public MessageEntity createMessageForUser(final Long conversationId, String text) {
        final MessageEntity message = new MessageEntity();
        message.setText(text);
        message.setUser(userBusiness.findConnectedUser());
        message.setConversation(conversationRepository.findConcreteById(conversationId));
        return messageRepository.save(message);
    }
}
