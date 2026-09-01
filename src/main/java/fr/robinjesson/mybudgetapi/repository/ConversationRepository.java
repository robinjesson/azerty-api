package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.ConversationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends FineRepository<ConversationEntity, Long> {
    List<ConversationEntity> findByParticipantsUid(String uid);
}
