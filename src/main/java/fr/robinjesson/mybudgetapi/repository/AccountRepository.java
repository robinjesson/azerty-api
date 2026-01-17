package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository  extends FineRepository<AccountEntity, Long> {
    List<AccountEntity> findByUserUid(String userUid);
}
