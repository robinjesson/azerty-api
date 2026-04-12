package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.ConversationEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends FineRepository<ConversationEntity, Long> {
}
