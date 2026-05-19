package task.manager.Task.Manager.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import task.manager.Task.Manager.dto.requests.UserCreateRequest;
import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.entity.Task;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.services.TaskService;
import task.manager.Task.Manager.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {

        this.userService = userService;
        this.taskService=taskService;
    }
    @GetMapping
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
    @GetMapping("{id}/tasks")
    public List<TaskResponse> getTasksByUserId(@PathVariable Long id){
        return taskService.getTasksByUser(id);
    }
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request){
        return userService.createUser(request);
    }
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
