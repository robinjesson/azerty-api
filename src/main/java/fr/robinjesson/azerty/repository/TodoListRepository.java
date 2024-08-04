package fr.robinjesson.azerty.repository;

import fr.robinjesson.azerty.entities.TodoListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoListRepository extends JpaRepository<TodoListEntity, Long> {
}
