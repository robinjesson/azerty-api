package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.TagAdapter;
import fr.robinjesson.mybudgetapi.api.request.TagRequest;
import fr.robinjesson.mybudgetapi.api.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagAdapter tagAdapter;

    @GetMapping
    public ResponseEntity<Set<TagResponse>> findAllTagsForConnectedUser() {
        return ResponseEntity.ok(tagAdapter.findAllTagsByUser());
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTagForConnectedUser(final TagRequest tagRequest) {
        return new ResponseEntity<>(tagAdapter.createTagForConnectedUser(tagRequest), HttpStatus.CREATED);
    }

}
