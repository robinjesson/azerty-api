package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.TagEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends FineRepository<TagEntity, Long> {
    List<TagEntity> findAllByOwnerUid(String ownerUid);
    
    Optional<TagEntity> findByLabelAndOwnerUid(String label, String ownerUid);
}
