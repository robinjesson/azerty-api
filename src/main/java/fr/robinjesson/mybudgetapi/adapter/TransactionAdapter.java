package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.TransactionCreationRequest;
import fr.robinjesson.mybudgetapi.api.request.TransactionUpdateRequest;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
import fr.robinjesson.mybudgetapi.businesses.TransactionBusiness;
import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import fr.robinjesson.mybudgetapi.mappers.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionAdapter {
    private final TransactionBusiness transactionBusiness;
    private final TransactionMapper transactionMapper;

    public TransactionResponse createTransaction(final UUID accountUuid, final TransactionCreationRequest request) {
        final TransactionEntity transactionEntity = transactionMapper.mapToEntity(request);
        final var tags = transactionBusiness.resolveTagsFromLabels(request.getTagLabels());
        transactionEntity.setTags(tags);

        final TransactionEntity savedTransaction = transactionBusiness.createTransaction(accountUuid, transactionEntity);
        return transactionMapper.mapToResponse(savedTransaction);
    }

    public TransactionResponse updateTransaction(final UUID accountUuid, final UUID transactionUuid, final TransactionUpdateRequest request) {
        final TransactionEntity updateData = new TransactionEntity();
        updateData.setAmount(request.getAmount());
        final var tags = transactionBusiness.resolveTagsFromLabels(request.getTagLabels());
        updateData.setTags(tags);

        final TransactionEntity updatedTransaction = transactionBusiness.updateTransaction(accountUuid, transactionUuid, updateData);
        return transactionMapper.mapToResponse(updatedTransaction);
    }

    public List<TransactionResponse> findTransactionsByAccount(final UUID accountUuid) {
        final List<TransactionEntity> transactions = transactionBusiness.findTransactionsByAccount(accountUuid);
        return transactionMapper.mapToResponse(transactions);
    }
}

