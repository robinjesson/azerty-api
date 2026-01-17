package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import fr.robinjesson.mybudgetapi.entities.TagEntity;
import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import fr.robinjesson.mybudgetapi.exception.ForbiddenException;
import fr.robinjesson.mybudgetapi.exception.NotFoundException;
import fr.robinjesson.mybudgetapi.repository.AccountRepository;
import fr.robinjesson.mybudgetapi.repository.TagRepository;
import fr.robinjesson.mybudgetapi.repository.TransactionRepository;
import fr.robinjesson.mybudgetapi.security.ConnectedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionBusiness {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final ConnectedUser connectedUser;

    public TransactionEntity createTransaction(final UUID accountUuid, final TransactionEntity transactionEntity) {
        final AccountEntity account = accountRepository.findById(accountUuid)
                .orElseThrow(() -> new NotFoundException("Account not found with uuid " + accountUuid));

        if (!account.getUser().getUid().equals(connectedUser.getUid())) {
            throw new ForbiddenException("Access denied to account " + accountUuid);
        }

        transactionEntity.setAccount(account);
        transactionEntity.setTransactionDate(LocalDate.now());
        transactionEntity.setIsPointed(false);
        transactionEntity.setIsReconciled(false);

        return transactionRepository.save(transactionEntity);
    }

    public TransactionEntity updateTransaction(final UUID accountUuid, final UUID transactionUuid, final TransactionEntity updateData) {
        final AccountEntity account = accountRepository.findById(accountUuid)
                .orElseThrow(() -> new NotFoundException("Account not found with uuid " + accountUuid));

        if (!account.getUser().getUid().equals(connectedUser.getUid())) {
            throw new ForbiddenException("Access denied to account " + accountUuid);
        }

        final TransactionEntity transaction = transactionRepository.findById(transactionUuid)
                .orElseThrow(() -> new NotFoundException("Transaction not found with uuid " + transactionUuid));

        if (!transaction.getAccount().getUuid().equals(accountUuid)) {
            throw new ForbiddenException("Transaction does not belong to account " + accountUuid);
        }

        transaction.setAmount(updateData.getAmount());
        if (updateData.getTags() != null && !updateData.getTags().isEmpty()) {
            transaction.setTags(updateData.getTags());
        }

        return transactionRepository.save(transaction);
    }

    public List<TransactionEntity> findTransactionsByAccount(final UUID accountUuid) {
        final AccountEntity account = accountRepository.findById(accountUuid)
                .orElseThrow(() -> new NotFoundException("Account not found with uuid " + accountUuid));

        if (!account.getUser().getUid().equals(connectedUser.getUid())) {
            throw new ForbiddenException("Access denied to account " + accountUuid);
        }

        return transactionRepository.findByAccountUuid(accountUuid);
    }

    public Set<TagEntity> resolveTagsFromLabels(final Set<String> tagLabels) {
        final Set<TagEntity> userTags = tagRepository.findAllByOwnerUid(connectedUser.getUid());
        return tagLabels.stream()
                .map(label -> userTags.stream()
                        .filter(tag -> tag.getLabel().equals(label))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Tag not found with label " + label)))
                .collect(Collectors.toSet());
    }
}

