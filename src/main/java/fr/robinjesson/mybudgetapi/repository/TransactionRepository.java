package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.account.uuid = :accountId AND t.isPointed = true")
    BigDecimal sumPointedAmountByAccount(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.account.uuid = :accountId AND t.isReconciled = true")
    BigDecimal sumReconciledAmountByAccount(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.account.uuid = :accountId")
    BigDecimal sumTotalAmountByAccount(@Param("accountId") UUID accountId);


}
