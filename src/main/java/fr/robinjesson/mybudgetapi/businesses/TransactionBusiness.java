package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import fr.robinjesson.mybudgetapi.entities.TagEntity;
import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import fr.robinjesson.mybudgetapi.entities.UserEntity;
import fr.robinjesson.mybudgetapi.exception.ForbiddenException;
import fr.robinjesson.mybudgetapi.exception.NotFoundException;
import fr.robinjesson.mybudgetapi.repository.AccountRepository;
import fr.robinjesson.mybudgetapi.repository.TagRepository;
import fr.robinjesson.mybudgetapi.repository.TransactionRepository;
import fr.robinjesson.mybudgetapi.repository.UserRepository;
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
    private final UserRepository userRepository;
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
        transactionEntity.setAccount(account);
        return transactionRepository.save(transactionEntity);
    }

    public List<TransactionEntity> findTransactionsByAccount(final Long accountId) {
        final AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with id " + accountId));

        if (!account.getUser().getUid().equals(connectedUser.getUid())) {
            throw new ForbiddenException("Access denied to account " + accountId);
        }

        return transactionRepository.findByAccountId(accountId);
    }

    public List<TagEntity> resolveTagsFromLabels(final Set<String> tagLabels) {
        final List<TagEntity> existingTags = tagRepository.findAllByOwnerUid(connectedUser.getUid());
        final UserEntity owner = userRepository.findById(connectedUser.getUid())
                .orElseThrow(() -> new NotFoundException("User not found for UID " + connectedUser.getUid()));

        // Partition labels into existing and new
        final Set<String> existingLabels = existingTags.stream()
                .map(TagEntity::getLabel)
                .collect(Collectors.toSet());
        
        final List<String> newLabels = tagLabels.stream()
                .filter(label -> !existingLabels.contains(label))
                .toList();

        // Batch create new tags
        final List<TagEntity> newTags = newLabels.isEmpty() ? List.of() :
                tagRepository.saveAll(newLabels.stream()
                        .map(label -> TagEntity.builder().label(label).owner(owner).build())
                        .toList());

        // Combine existing and newly created tags, preserving order from input
        return tagLabels.stream()
                .map(label -> existingTags.stream()
                        .filter(tag -> tag.getLabel().equals(label))
                        .findFirst()
                        .orElseGet(() -> newTags.stream()
                                .filter(tag -> tag.getLabel().equals(label))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Tag not found: " + label))))
                .toList();
    }
}

