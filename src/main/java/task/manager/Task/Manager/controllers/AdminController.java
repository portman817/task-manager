package task.manager.Task.Manager.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.manager.Task.Manager.dto.requests.admin.*;
import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.services.TaskService;
import task.manager.Task.Manager.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final TaskService taskService;

    public AdminController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }
    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
    @PutMapping("/users/{id}")
    public UserResponse updateUser(@PathVariable Long id,@Valid @RequestBody AdminUserUpdateRequest request){
        return userService.updateUser(id, request);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/users/{userId}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long userId, @Valid @RequestBody AdminChangePasswordRequest request){
        userService.changePassword(userId,request);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/users")
    public UserResponse createUser(@Valid @RequestBody AdminUserCreateRequest request){
        return userService.createUser(request);
    }
    @GetMapping("/tasks")
    public List<TaskResponse> getAllTasks(){
        return taskService.getAllTasks();
    }
    @GetMapping("/tasks/{id}")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }
    @GetMapping("/users/{userId}/tasks")
    public List<TaskResponse> getTasksByUserId(@PathVariable Long userId){
        return taskService.getTasksByUserId(userId);
    }
    @PostMapping("/tasks")
    public TaskResponse createTask(@Valid @RequestBody AdminTaskCreateRequest request){
        return taskService.createTask(request);
    }
    @PatchMapping("/tasks/{taskId}")
    public TaskResponse updateTask(@PathVariable Long taskId, @Valid @RequestBody AdminUpdateTaskRequest request){
        return taskService.updateTask(taskId, request);
    }
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
