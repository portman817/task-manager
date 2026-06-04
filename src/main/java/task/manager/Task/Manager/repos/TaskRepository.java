package task.manager.Task.Manager.repos;

import org.springframework.data.repository.CrudRepository;
import task.manager.Task.Manager.entity.Task;
import task.manager.Task.Manager.entity.User;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    boolean existsByOwner(User user);
    List<Task> findByOwnerUserId(Long userId);

}
