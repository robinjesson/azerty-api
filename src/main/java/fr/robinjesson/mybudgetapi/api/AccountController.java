package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.AccountAdapter;
import fr.robinjesson.mybudgetapi.api.request.AccountCreationRequest;
import fr.robinjesson.mybudgetapi.api.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountAdapter accountAdapter;

    @GetMapping("/{uuid}")
    public ResponseEntity<AccountResponse> findById(final UUID uuid) {
        return ResponseEntity.ok(accountAdapter.findConcreteById(uuid));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findUserAccounts() {
        return ResponseEntity.ok(accountAdapter.findUserAccounts());
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccountForConnectedUser(@RequestBody final AccountCreationRequest accountCreationRequest) {
        return new ResponseEntity<>(accountAdapter.createAccountForConnectedUser(accountCreationRequest), HttpStatus.CREATED);

    }
}
