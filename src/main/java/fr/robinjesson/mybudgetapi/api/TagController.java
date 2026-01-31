package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.TagAdapter;
import fr.robinjesson.mybudgetapi.api.request.TagRequest;
import fr.robinjesson.mybudgetapi.api.response.TagResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagAdapter tagAdapter;

    @GetMapping
    @Operation(summary = "Get all user's tags")
    public ResponseEntity<List<TagResponse>> findAllTagsForConnectedUser() {
        return ResponseEntity.ok(tagAdapter.findAllTagsByUser());
    }

    @PostMapping
    @Operation(summary = "Create a new tag")
    public ResponseEntity<TagResponse> createTagForConnectedUser(@RequestBody @Valid final TagRequest tagRequest) {
        return new ResponseEntity<>(tagAdapter.createTagForConnectedUser(tagRequest), HttpStatus.CREATED);
    }

}
