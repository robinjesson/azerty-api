package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository  extends FineRepository<AccountEntity, UUID> {
    List<AccountEntity> findByUserUid(String userUid);
}
