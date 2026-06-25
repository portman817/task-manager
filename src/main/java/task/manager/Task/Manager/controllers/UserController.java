package task.manager.Task.Manager.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.manager.Task.Manager.dto.requests.user.CurrentUserChangePasswordRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserCreateTaskRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserUpdateTaskRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserUpdateUsernameRequest;
import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.services.TaskService;
import task.manager.Task.Manager.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users/me")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {

        this.userService = userService;
        this.taskService=taskService;
    }


    @GetMapping("/tasks")
    public List<TaskResponse> getTasksByCurrentUser(){
        return taskService.getTasksByCurrentUser();
    }
    @PostMapping("/tasks")
    public TaskResponse createTaskByCurrentUser(@Valid @RequestBody CurrentUserCreateTaskRequest request){
        return taskService.createTaskByCurrentUser(request);
    }


    @GetMapping
    public UserResponse getCurrentUser(){
        return userService.getCurrentUser();
    }
    @PutMapping
    public UserResponse currentUserUpdateUsername(@Valid @RequestBody CurrentUserUpdateUsernameRequest request){
        return userService.currentUserUpdateUsername(request);
    }
    @PutMapping("/password")
    public ResponseEntity<Void> currentUserChangePassword(@Valid @RequestBody CurrentUserChangePasswordRequest request){
         userService.currentUserChangePassword(request);
         return ResponseEntity.noContent().build();
    }
    @PatchMapping("/tasks/{id}")
    public TaskResponse updateTaskByCurrentUser(@PathVariable Long id, @Valid @RequestBody CurrentUserUpdateTaskRequest request){
        return taskService.currentUserUpdateTask(id, request);
    }
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> currentUserDeleteTask(@PathVariable Long id){
        taskService.deleteTaskByCurrentUser(id);
        return ResponseEntity.noContent().build();
    }
}
