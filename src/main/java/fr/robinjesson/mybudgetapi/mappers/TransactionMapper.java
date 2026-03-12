package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.request.TransactionRequest;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapperConfiguration.class, uses = AccountMapper.class)
public interface TransactionMapper {
    TransactionEntity mapToEntity(TransactionRequest source);

    List<TransactionResponse> mapToResponse(List<TransactionEntity> source);

    TransactionResponse mapToResponse(TransactionEntity source);

    void mapToExistingEntity(@MappingTarget TransactionEntity target, TransactionRequest source);
}

