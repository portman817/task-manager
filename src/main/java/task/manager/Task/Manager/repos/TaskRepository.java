package task.manager.Task.Manager.repos;

import org.springframework.data.repository.CrudRepository;
import task.manager.Task.Manager.entity.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByOwnerUserId(Long userId);

}
