package fr.robinjesson.azertyapi.repository;

import fr.robinjesson.azertyapi.entities.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends JpaRepository<TextEntity, Long> {
}
