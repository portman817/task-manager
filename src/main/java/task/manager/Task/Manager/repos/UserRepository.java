package task.manager.Task.Manager.repos;

import org.springframework.data.repository.CrudRepository;
import task.manager.Task.Manager.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername (String username);
}
