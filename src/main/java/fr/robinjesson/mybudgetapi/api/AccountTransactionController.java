package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.TransactionAdapter;
import fr.robinjesson.mybudgetapi.api.assembler.TransactionModelAssembler;
import fr.robinjesson.mybudgetapi.api.request.TransactionRequest;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
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
@RequestMapping("/v0/accounts/{accountId}/transactions")
@RequiredArgsConstructor
@Tag(name = "Account Transactions", description = "Manage transactions scoped to a specific account")
public class AccountTransactionController {

    private final TransactionAdapter transactionAdapter;
    private final TransactionModelAssembler transactionModelAssembler;

    @GetMapping
    @Operation(summary = "Get all transactions for a given account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions"),
            @ApiResponse(responseCode = "403", description = "Access denied to this account"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<List<EntityModel<TransactionResponse>>> findTransactionsByAccount(
            @PathVariable final Long accountId) {
        return ResponseEntity.ok(transactionAdapter.findTransactionsByAccount(accountId).stream()
                .map(transactionModelAssembler::toModel)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Create a new transaction on a given account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied to this account"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<EntityModel<TransactionResponse>> createTransaction(
            @PathVariable final Long accountId,
            @RequestBody @Valid final TransactionRequest request) {
        return new ResponseEntity<>(
                transactionModelAssembler.toModel(transactionAdapter.createTransaction(accountId, request)),
                HttpStatus.CREATED);
    }
}
