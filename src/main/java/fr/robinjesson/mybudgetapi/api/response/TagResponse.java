package fr.robinjesson.mybudgetapi.api.response;

import fr.robinjesson.mybudgetapi.entities.enums.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponse {
    private String label;
    private Category category;
}
