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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v0/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Manage transactions across all accounts of the connected user")
public class TransactionController {
    private final TransactionAdapter transactionAdapter;
    private final TransactionModelAssembler transactionModelAssembler;

    @GetMapping
    @Operation(summary = "Get all transactions for the connected user, across all accounts")
    @ApiResponse(responseCode = "200", description = "List of all user transactions")
    public ResponseEntity<List<EntityModel<TransactionResponse>>> findAllTransactions() {
        return ResponseEntity.ok(transactionAdapter.findAllTransactions().stream()
                .map(transactionModelAssembler::toModel)
                .toList());
    }

    @PutMapping("/{transactionId}")
    @Operation(summary = "Update an existing transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied to this transaction"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<EntityModel<TransactionResponse>> updateTransaction(
            @PathVariable final Long transactionId,
            @RequestBody @Valid final TransactionRequest request) {
        return ResponseEntity.ok(
                transactionModelAssembler.toModel(transactionAdapter.updateTransaction(transactionId, request)));
    }
}

