package fr.robinjesson.azertyapi.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "todo")
@Data
public class TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_todo_list_id")
    private TodoListEntity todoList;

    @Embedded
    private Timestamp timestamp;
}
