package task.manager.Task.Manager.repos;

import org.springframework.data.repository.CrudRepository;
import task.manager.Task.Manager.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
