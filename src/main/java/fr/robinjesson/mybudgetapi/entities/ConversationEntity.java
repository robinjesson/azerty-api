package fr.robinjesson.mybudgetapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "conversation")
@Getter
@Setter
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToMany
    @JoinTable(
            name = "conversations_participants",
            joinColumns = @JoinColumn(name = "fk_conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_participant_id")
    )
    private List<UserEntity> participants;

    @Embedded
    private Timestamp timestamp;
}
