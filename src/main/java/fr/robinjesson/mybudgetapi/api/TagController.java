package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.TagAdapter;
import fr.robinjesson.mybudgetapi.api.assembler.TagModelAssembler;
import fr.robinjesson.mybudgetapi.api.request.TagRequest;
import fr.robinjesson.mybudgetapi.api.response.TagResponse;
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
@RequestMapping("/v0/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Manage tags used to categorise transactions")
public class TagController {
    private final TagAdapter tagAdapter;
    private final TagModelAssembler tagModelAssembler;

    @GetMapping
    @Operation(summary = "Get all tags for the connected user")
    @ApiResponse(responseCode = "200", description = "List of tags")
    public ResponseEntity<List<EntityModel<TagResponse>>> findAllTagsForConnectedUser() {
        return ResponseEntity.ok(tagAdapter.findAllTagsByUser().stream()
                .map(tagModelAssembler::toModel)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Create a new tag for the connected user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<EntityModel<TagResponse>> createTagForConnectedUser(
            @RequestBody @Valid final TagRequest tagRequest) {
        return new ResponseEntity<>(
                tagModelAssembler.toModel(tagAdapter.createTagForConnectedUser(tagRequest)),
                HttpStatus.CREATED);
    }
}
