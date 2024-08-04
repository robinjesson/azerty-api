package fr.robinjesson.azerty.repository;

import fr.robinjesson.azerty.entities.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<TextEntity, Long> {
}
