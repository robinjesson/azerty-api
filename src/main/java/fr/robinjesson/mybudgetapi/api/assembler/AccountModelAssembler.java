package fr.robinjesson.mybudgetapi.api.assembler;

import fr.robinjesson.mybudgetapi.api.AccountController;
import fr.robinjesson.mybudgetapi.api.response.AccountResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountModelAssembler implements RepresentationModelAssembler<AccountResponse, EntityModel<AccountResponse>> {

    @Override
    public EntityModel<AccountResponse> toModel(final AccountResponse response) {
        return EntityModel.of(response,
                linkTo(methodOn(AccountController.class).findById(response.id())).withSelfRel(),
                linkTo(methodOn(AccountController.class).findUserAccounts()).withRel("accounts"));
    }
}
