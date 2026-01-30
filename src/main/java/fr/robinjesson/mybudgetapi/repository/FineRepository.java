package fr.robinjesson.mybudgetapi.repository;

import fr.robinjesson.mybudgetapi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface FineRepository<T, ID> extends JpaRepository<T, ID> {

    default T findConcreteById(final ID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Entity not found with id: " + id));
    }
}
