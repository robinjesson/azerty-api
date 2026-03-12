package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.adapter.UserAdapter;
import fr.robinjesson.mybudgetapi.api.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Retrieve information about the connected user")
public class UserController {

    private final UserAdapter userAdapter;

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<EntityModel<UserResponse>> getMe() {
        final UserResponse userResponse = userAdapter.getMe();
        return ResponseEntity.ok(EntityModel.of(userResponse,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getMe()).withSelfRel()));
    }
}
