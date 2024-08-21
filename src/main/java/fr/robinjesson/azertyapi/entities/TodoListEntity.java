package fr.robinjesson.azertyapi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "todo_list")
@Data
public class TodoListEntity extends ElementEntity {

    @OneToMany(mappedBy = "todoList")
    private List<TodoEntity> todos;
}
