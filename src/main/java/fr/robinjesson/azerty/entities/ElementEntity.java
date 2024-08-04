package fr.robinjesson.azerty.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "element")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class ElementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Timestamp timestamp;
}
