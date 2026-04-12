package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.MessageEntity;
import fr.robinjesson.mybudgetapi.exception.NotFoundException;
import fr.robinjesson.mybudgetapi.repository.ConversationRepository;
import fr.robinjesson.mybudgetapi.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationBusiness {
    private final ConversationRepository conversationRepository;
    private final UserBusiness userBusiness;
    private final MessageRepository messageRepository;
    private final Sinks.Many<ServerSentEvent<MessageEntity>> globalSink = Sinks.many()
            .multicast()
            .directBestEffort();

    public List<MessageEntity> findMessagesByConversationId(final Long conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new NotFoundException("Conversation not found");
        }
        return messageRepository.findByConversationId(conversationId);
    }

    public Flux<ServerSentEvent<MessageEntity>> getMessageStream(final Long conversationId) {
        return globalSink.asFlux()
                .filter(event ->
                        event.data() != null && event.data().getConversation().getId().equals(conversationId)
                )
                .startWith(ServerSentEvent.<MessageEntity>builder().event("connected").data(null).build());
    }

    public MessageEntity createMessageForUser(final Long conversationId, String text) {
        MessageEntity message = new MessageEntity();
        message.setText(text);
        message.setUser(userBusiness.findConnectedUser());
        message.setConversation(conversationRepository.findConcreteById(conversationId));
        message = messageRepository.save(message);

        ServerSentEvent<MessageEntity> event = ServerSentEvent.<MessageEntity>builder()
                .event("message")
                .data(message)
                .id(UUID.randomUUID().toString())
                .build();

        globalSink.tryEmitNext(event);

        return message;
    }
}
