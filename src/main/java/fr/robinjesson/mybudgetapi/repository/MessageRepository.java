package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.MessageEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends FineRepository<MessageEntity, Long> {
    List<MessageEntity> findByConversationId(Long conversationId);
}
