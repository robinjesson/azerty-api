package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.AccountAdapter;
import fr.robinjesson.mybudgetapi.api.assembler.AccountModelAssembler;
import fr.robinjesson.mybudgetapi.api.request.AccountCreationRequest;
import fr.robinjesson.mybudgetapi.api.response.AccountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v0/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Manage bank accounts for the connected user")
public class AccountController {

    private final AccountAdapter accountAdapter;
    private final AccountModelAssembler accountModelAssembler;

    @GetMapping("/{id}")
    @Operation(summary = "Get one account by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "403", description = "Access denied to this account"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<EntityModel<AccountResponse>> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(accountModelAssembler.toModel(accountAdapter.findConcreteById(id)));
    }

    @GetMapping
    @Operation(summary = "Get all accounts for the connected user")
    @ApiResponse(responseCode = "200", description = "List of accounts")
    public ResponseEntity<List<EntityModel<AccountResponse>>> findUserAccounts() {
        return ResponseEntity.ok(accountAdapter.findUserAccounts().stream()
                .map(accountModelAssembler::toModel)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Create a new account for the connected user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<EntityModel<AccountResponse>> createAccountForConnectedUser(
            @RequestBody @Valid final AccountCreationRequest accountCreationRequest) {
        return new ResponseEntity<>(
                accountModelAssembler.toModel(accountAdapter.createAccountForConnectedUser(accountCreationRequest)),
                HttpStatus.CREATED);
    }
}
