package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.TransactionAdapter;
import fr.robinjesson.mybudgetapi.api.request.TransactionCreationRequest;
import fr.robinjesson.mybudgetapi.api.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts/{accountUuid}/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionAdapter transactionAdapter;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable final UUID accountUuid, @RequestBody final TransactionCreationRequest request) {
        return new ResponseEntity<>(transactionAdapter.createTransaction(accountUuid, request), HttpStatus.CREATED);
    }

    @PutMapping("/{transactionUuid}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable final UUID accountUuid, @PathVariable final UUID transactionUuid, @RequestBody final TransactionRequest request) {
        return ResponseEntity.ok(transactionAdapter.updateTransaction(accountUuid, transactionUuid, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findTransactionsByAccount(@PathVariable final UUID accountUuid) {
        return ResponseEntity.ok(transactionAdapter.findTransactionsByAccount(accountUuid));
    }
}

