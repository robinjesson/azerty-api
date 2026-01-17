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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionBusiness {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final ConnectedUser connectedUser;

    public TransactionEntity findConcreteById(final Long transactionId) {
        final var transaction = transactionRepository.findConcreteById(transactionId);
        if (!transaction.getAccount().getUser().getUid().equals(connectedUser.getUid()))
            throw new ForbiddenException("Access denied to account " + transaction.getAccount().getId());
        return transaction;
    }

    public TransactionEntity save(final TransactionEntity transactionEntity, final AccountEntity account) {
        if (!account.getUser().getUid().equals(connectedUser.getUid()))
            throw new ForbiddenException("Access denied to account " + account.getId());
        return transactionRepository.save(transactionEntity);
    }

    public List<TransactionEntity> findTransactionsByAccount(final Long accountId) {
        final AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with uuid " + accountId));

        if (!account.getUser().getUid().equals(connectedUser.getUid())) {
            throw new ForbiddenException("Access denied to account " + accountId);
        }

        return transactionRepository.findByAccountId(accountId);
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

