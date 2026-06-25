package task.manager.Task.Manager.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.dto.requests.user.CurrentUserCreateTaskRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserUpdateTaskRequest;
import task.manager.Task.Manager.dto.requests.admin.AdminUpdateTaskRequest;
import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.entity.Task;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.mappers.TaskMapper;
import task.manager.Task.Manager.repos.TaskRepository;
import task.manager.Task.Manager.repos.UserRepository;
import task.manager.Task.Manager.dto.requests.admin.AdminTaskCreateRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Iterable<Task> tasks = taskRepository.findAll();
        List<TaskResponse> result = new ArrayList<>();
        for(Task task : tasks){
            result.add(TaskMapper.toResponse(task));
        }
        return result;
    }
    public TaskResponse getTaskById(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return TaskMapper.toResponse(task);
    }
    public List<TaskResponse> getTasksByUserId(Long id){
        User user = userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Iterable<Task> taskByUserId = taskRepository.findByOwnerUserId(id);
        List<TaskResponse> result = new ArrayList<>();
        for(Task task : taskByUserId){
            result.add(TaskMapper.toResponse(task));
        }
        return result;
    }
    public TaskResponse createTask(AdminTaskCreateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getTaskStatus());
        task.setOwner(user);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        Task savedTask=taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);
    }
    public TaskResponse updateTask(Long taskId, AdminUpdateTaskRequest request){
        Task task = taskRepository.findById(taskId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if(request.getTitle()==null && request.getDescription()==null && request.getTaskStatus()==null && request.getUserId()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be provided");
        }
        if(request.getUserId() != null && task.getOwner().getUserId() != request.getUserId()){
            User user = userRepository.findById(request.getUserId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            task.setOwner(user);
        }
        if(request.getTitle()!=null){
            task.setTitle(request.getTitle());
        }
        if(request.getDescription()!=null){
            task.setDescription(request.getDescription());
        }
        if(request.getTaskStatus()!=null){
            task.setStatus(request.getTaskStatus());
        }
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask=taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);
    }
    public TaskResponse currentUserUpdateTask(Long taskId, CurrentUserUpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if(!task.getOwner().equals(currentUser)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if(request.getTitle() == null && request.getTaskStatus() == null && request.getDescription() ==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be provided");
        }
        if(request.getTitle() != null){
            task.setTitle(request.getTitle());
        }
        if(request.getTaskStatus() !=null){
            task.setStatus(request.getTaskStatus());
        }
        if(request.getDescription() !=null){
            task.setDescription(request.getDescription());
        }
        task.setUpdatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);
    }
    public void deleteTask(Long id) {
        Task task =taskRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.deleteById(id);
    }
    public List<TaskResponse> getTasksByCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Iterable<Task> tasks = taskRepository.findByOwnerUserId(currentUser.getUserId());
        List<TaskResponse> result = new ArrayList<>();
        for(Task task : tasks){
            result.add(TaskMapper.toResponse(task));
        }
        return result;
    }
    public TaskResponse createTaskByCurrentUser(CurrentUserCreateTaskRequest request){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getTaskStatus());
        task.setOwner(currentUser);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);

    }
    public void deleteTaskByCurrentUser(Long id){
        Task task = taskRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean isOwner = currentUser.getUserId() == task.getOwner().getUserId();
        if(!isOwner){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can delete only your own tasks");
        }
        taskRepository.delete(task);
    }

}
