package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.TransactionAdapter;
import fr.robinjesson.mybudgetapi.api.request.TransactionRequest;
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
    public ResponseEntity<TransactionResponse> createTransaction(@RequestParam final Long accountId, @RequestBody final TransactionRequest request) {
        return new ResponseEntity<>(transactionAdapter.createTransaction(accountId, request), HttpStatus.CREATED);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable final Long transactionId, @RequestBody final TransactionRequest request) {
        return ResponseEntity.ok(transactionAdapter.updateTransaction(transactionId, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findTransactionsByAccount(@RequestParam final Long accountId) {
        return ResponseEntity.ok(transactionAdapter.findTransactionsByAccount(accountId));
    }
}

