package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.AccountCreationRequest;
import fr.robinjesson.mybudgetapi.api.response.AccountResponse;
import fr.robinjesson.mybudgetapi.businesses.AccountBusiness;
import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import fr.robinjesson.mybudgetapi.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountAdapter {

    private final AccountBusiness accountBusiness;
    private final AccountMapper accountMapper;

    public AccountResponse findConcreteById(final Long id) {
        final AccountEntity accountEntity = accountBusiness.findConcreteById(id);
        return accountMapper.mapToResponse(accountEntity);
    }

    public List<AccountResponse> findUserAccounts() {
        final List<AccountEntity> accountEntities = accountBusiness.findUserAccounts();
        return accountMapper.mapToResponse(accountEntities);
    }

    public AccountResponse createAccountForConnectedUser(final AccountCreationRequest accountCreationRequest) {
        final AccountEntity accountEntity = accountMapper.mapToEntity(accountCreationRequest);
        final AccountEntity savedAccount = accountBusiness.createAccountForConnectedUser(accountEntity);
        return accountMapper.mapToResponse(savedAccount);
    }

}
