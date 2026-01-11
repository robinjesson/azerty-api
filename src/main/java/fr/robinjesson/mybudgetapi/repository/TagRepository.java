package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.TagEntity;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TagRepository extends FineRepository<TagEntity, String> {
    Set<TagEntity> findAllByOwnerUid(String uid);
}
