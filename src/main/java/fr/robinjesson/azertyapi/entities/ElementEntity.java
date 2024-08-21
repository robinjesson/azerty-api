package fr.robinjesson.azertyapi.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "element")
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class ElementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Timestamp timestamp;
}
