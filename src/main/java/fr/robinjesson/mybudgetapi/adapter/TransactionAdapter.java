package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.TransactionRequest;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
import fr.robinjesson.mybudgetapi.businesses.AccountBusiness;
import fr.robinjesson.mybudgetapi.businesses.TransactionBusiness;
import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import fr.robinjesson.mybudgetapi.mappers.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionAdapter {
    private final TransactionBusiness transactionBusiness;
    private final TransactionMapper transactionMapper;
    private final AccountBusiness accountBusiness;

    public TransactionResponse createTransaction(final Long accountid, final TransactionRequest request) {
        final TransactionEntity transactionEntity = transactionMapper.mapToEntity(request);
        final var tags = transactionBusiness.resolveTagsFromLabels(request.getTagLabels());
        transactionEntity.setTags(tags);
        transactionEntity.setIsPointed(false);
        transactionEntity.setIsReconciled(false);
        transactionEntity.setTransactionDate(java.time.LocalDate.now());
        final TransactionEntity savedTransaction = transactionBusiness.save(transactionEntity, accountBusiness.findConcreteById(accountid));
        return transactionMapper.mapToResponse(savedTransaction);
    }

    public TransactionResponse updateTransaction(final Long transactionId, final TransactionRequest request) {
        final TransactionEntity transaction = transactionBusiness.findConcreteById(transactionId);
        transactionMapper.mapToExistingEntity(transaction, request);
        final var tags = transactionBusiness.resolveTagsFromLabels(request.getTagLabels());
        transaction.setTags(tags);
        final TransactionEntity updatedTransaction = transactionBusiness.save(transaction, transaction.getAccount());
        return transactionMapper.mapToResponse(updatedTransaction);
    }

    public List<TransactionResponse> findTransactionsByAccount(final Long accountId) {
        final List<TransactionEntity> transactions = transactionBusiness.findTransactionsByAccount(accountId);
        return transactionMapper.mapToResponse(transactions);
    }
}

