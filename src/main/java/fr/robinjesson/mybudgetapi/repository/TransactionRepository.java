package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends FineRepository<TransactionEntity, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.account.id = :accountId AND t.isPointed = true")
    BigDecimal sumPointedAmountByAccount(@Param("accountId") Long accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.account.id = :accountId AND t.isReconciled = true")
    BigDecimal sumReconciledAmountByAccount(@Param("accountId") Long accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.account.id = :accountId")
    BigDecimal sumTotalAmountByAccount(@Param("accountId") Long accountId);

    @Query("SELECT DISTINCT t FROM TransactionEntity t LEFT JOIN FETCH t.tags WHERE t.account.id = :accountId")
    List<TransactionEntity> findByAccountId(@Param("accountId") Long accountId);

}
