package fr.robinjesson.azertyapi.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "document")
@Data
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private UserEntity user;

    @Embedded
    private Timestamp timestamp;
}
