package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.request.AccountCreationRequest;
import fr.robinjesson.mybudgetapi.api.response.AccountResponse;
import fr.robinjesson.mybudgetapi.entities.AccountEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface AccountMapper {
    AccountEntity mapToEntity(AccountCreationRequest source);
    List<AccountResponse> mapToResponse(List<AccountEntity> source);
    AccountResponse mapToResponse(AccountEntity source);
}
