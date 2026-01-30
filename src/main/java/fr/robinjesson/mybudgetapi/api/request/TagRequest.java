package fr.robinjesson.mybudgetapi.api.request;

import fr.robinjesson.mybudgetapi.entities.enums.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {
    @NotBlank
    private String label;
    private Category category;
}
