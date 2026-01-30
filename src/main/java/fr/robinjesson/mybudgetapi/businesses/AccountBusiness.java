package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import fr.robinjesson.mybudgetapi.exception.ForbiddenException;
import fr.robinjesson.mybudgetapi.repository.AccountRepository;
import fr.robinjesson.mybudgetapi.repository.UserRepository;
import fr.robinjesson.mybudgetapi.security.ConnectedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountBusiness {
    private final AccountRepository accountRepository;
    private final ConnectedUser connectedUser;
    private final UserRepository userRepository;

    public AccountEntity findConcreteById(final Long accountId) {
        final AccountEntity accountEntity = accountRepository.findConcreteById(accountId);
        if(!accountEntity.getUser().getUid().equals(connectedUser.getUid()))
            throw new ForbiddenException("Access denied to account " + accountId);
        return accountEntity;
    }

    public List<AccountEntity> findUserAccounts() {
        return accountRepository.findByUserUid(connectedUser.getUid());
    }

    public AccountEntity createAccountForConnectedUser(final AccountEntity accountEntity) {
        return userRepository.findById(connectedUser.getUid())
                .map(user -> {
                    accountEntity.setUser(user);
                    return accountRepository.save(accountEntity);
                })
                .orElseThrow(() -> new IllegalStateException("User not found for UID " + connectedUser.getUid()));
    }

}
