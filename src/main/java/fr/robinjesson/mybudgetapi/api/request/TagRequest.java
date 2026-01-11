package fr.robinjesson.mybudgetapi.api.request;

import fr.robinjesson.mybudgetapi.entities.enums.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {
    private String label;
    private Category category;
}
