package fr.robinjesson.mybudgetapi.entities;

import fr.robinjesson.mybudgetapi.entities.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"label", "fk_owner_uid"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class TagEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ToString.Include
    @Column(nullable = false)
    private String label;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_owner_uid", nullable = false)
    private UserEntity owner;

    private Category category;
}
