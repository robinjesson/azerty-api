package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.TransactionRequest;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
import fr.robinjesson.mybudgetapi.businesses.AccountBusiness;
import fr.robinjesson.mybudgetapi.businesses.TransactionBusiness;
import fr.robinjesson.mybudgetapi.entities.TagEntity;
import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import fr.robinjesson.mybudgetapi.mappers.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionAdapter {
    private final TransactionBusiness transactionBusiness;
    private final TransactionMapper transactionMapper;
    private final AccountBusiness accountBusiness;

    public TransactionResponse createTransaction(final Long accountId, final TransactionRequest request) {
        final TransactionEntity transactionEntity = transactionMapper.mapToEntity(request);
        final List<TagEntity> tags = transactionBusiness.resolveTagsFromLabels(request.getTagLabels());
        transactionEntity.setTags(tags);
        transactionEntity.setIsPointed(false);
        transactionEntity.setIsReconciled(false);
        transactionEntity.setTransactionDate(LocalDate.now());
        final TransactionEntity savedTransaction = transactionBusiness.save(transactionEntity, accountBusiness.findConcreteById(accountId));
        return transactionMapper.mapToResponse(savedTransaction);
    }

    public TransactionResponse updateTransaction(final Long transactionId, final TransactionRequest request) {
        final TransactionEntity transaction = transactionBusiness.findConcreteById(transactionId);
        transactionMapper.mapToExistingEntity(transaction, request);
        final List<TagEntity> tags = transactionBusiness.resolveTagsFromLabels(request.getTagLabels());
        transaction.setTags(tags);
        final TransactionEntity updatedTransaction = transactionBusiness.save(transaction, transaction.getAccount());
        return transactionMapper.mapToResponse(updatedTransaction);
    }

    public List<TransactionResponse> findTransactionsByAccount(final Long accountId) {
        final List<TransactionEntity> transactions = transactionBusiness.findTransactionsByAccount(accountId);
        return transactionMapper.mapToResponse(transactions);
    }
}

