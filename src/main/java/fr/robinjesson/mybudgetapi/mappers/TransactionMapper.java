package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.request.TransactionCreationRequest;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
import fr.robinjesson.mybudgetapi.entities.TransactionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface TransactionMapper {
    TransactionEntity mapToEntity(TransactionCreationRequest source);

    List<TransactionResponse> mapToResponse(List<TransactionEntity> source);

    TransactionResponse mapToResponse(TransactionEntity source);
}

