package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.AccountAdapter;
import fr.robinjesson.mybudgetapi.api.request.AccountCreationRequest;
import fr.robinjesson.mybudgetapi.api.response.AccountResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountAdapter accountAdapter;

    @GetMapping("/{id}")
    @Operation(summary = "Get one account by id")
    public ResponseEntity<AccountResponse> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(accountAdapter.findConcreteById(id));
    }

    @GetMapping
    @Operation(summary = "Get all user's accounts")
    public ResponseEntity<List<AccountResponse>> findUserAccounts() {
        return ResponseEntity.ok(accountAdapter.findUserAccounts());
    }

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<AccountResponse> createAccountForConnectedUser(@RequestBody @Valid final AccountCreationRequest accountCreationRequest) {
        return new ResponseEntity<>(accountAdapter.createAccountForConnectedUser(accountCreationRequest), HttpStatus.CREATED);
    }
}
