package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.AccountEntity;
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

    public List<AccountEntity> findUserAccounts() {
        return accountRepository.findByUserUid(connectedUser.getUid());
    }

    public AccountEntity createAccountForConnectedUser(final AccountEntity accountEntity) {
        return userRepository.findById(connectedUser.getUid())
                .map(user -> {
                    accountEntity.setUser(user);
                    return accountRepository.save(accountEntity);
                })
                .orElse(null);
    }

}
