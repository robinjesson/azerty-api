package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository  extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findByUserUid(String userUid);
}
