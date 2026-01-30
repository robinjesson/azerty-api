package fr.robinjesson.mybudgetapi.entities;

import fr.robinjesson.mybudgetapi.entities.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class TagEntity {
    @Id
    @ToString.Include
    private String label;

    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_owner_uid")
    private UserEntity owner;
}
