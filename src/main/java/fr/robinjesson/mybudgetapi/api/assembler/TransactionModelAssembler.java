package fr.robinjesson.mybudgetapi.api.assembler;

import fr.robinjesson.mybudgetapi.api.AccountTransactionController;
import fr.robinjesson.mybudgetapi.api.TransactionController;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TransactionModelAssembler implements RepresentationModelAssembler<TransactionResponse, EntityModel<TransactionResponse>> {

    @Override
    public EntityModel<TransactionResponse> toModel(final TransactionResponse response) {
        return EntityModel.of(response,
                linkTo(methodOn(TransactionController.class).findAllTransactions()).withSelfRel(),
                linkTo(methodOn(AccountTransactionController.class).findTransactionsByAccount(response.account().id())).withRel("account-transactions"));
    }
}
