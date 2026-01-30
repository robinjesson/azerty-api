package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.TagEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends FineRepository<TagEntity, String> {
    List<TagEntity> findAllByOwnerUid(String uid);
}
