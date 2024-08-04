package fr.robinjesson.azerty.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "todo_list")
@Getter
@Setter
public class TodoListEntity extends ElementEntity {

    @OneToMany(mappedBy = "todoList")
    private List<TodoEntity> todos;
}
