package task.manager.Task.Manager.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.entity.Task;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.enums.TaskStatus;
import task.manager.Task.Manager.mappers.TaskMapper;
import task.manager.Task.Manager.repos.TaskRepository;
import task.manager.Task.Manager.repos.UserRepository;
import task.manager.Task.Manager.dto.requests.TaskCreateRequest;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskReposetory, UserRepository userRepository) {

        this.taskRepository = taskReposetory;
        this.userRepository = userRepository;
    }
    public List<TaskResponse> getAllTasks() {
        return ((List<Task>) taskRepository.findAll())
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }
    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }
    public Task createTask(TaskCreateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getTaskStatus());
        task.setOwner(user);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);
    }
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    public List<TaskResponse> getTasksByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return taskRepository.findByOwnerUserId(userId).stream().map(TaskMapper::toResponse).toList();
    }

}
