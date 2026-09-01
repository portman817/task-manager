package task.manager.Task.Manager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Administration", description = "Administrative operations for managing users and tasks")
public class AdminController {
    private final UserService userService;
    private final TaskService taskService;

    public AdminController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }
    @Operation(summary = "Get user", description = "Get user by id. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
    @Operation(summary = "Update user", description = "Partially updates the specified user. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PatchMapping("/users/{id}")
    public UserResponse updateUser(@PathVariable Long id,@Valid @RequestBody AdminUserUpdateRequest request){
        return userService.updateUser(id, request);
    }
    @Operation(summary = "Delete user", description = "Deletes the specified user and all associated tasks. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Change user password", description = "Changes the password of the specified user. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or new password matches the current password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/users/{userId}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long userId, @Valid @RequestBody AdminChangePasswordRequest request){
        userService.changePassword(userId,request);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Create user", description = "Creates a new user account. Unlike public registration, " +
            "this endpoint allows administrators to create users with the ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserResponse createUser(@Valid @RequestBody AdminUserCreateRequest request){
        return userService.createUser(request);
    }
    @Operation(summary = "Get all tasks", description = "Returns a list of all tasks in the system. Accessible only" +
            " to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of tasks returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/tasks")
    public List<TaskResponse> getAllTasks(){
        return taskService.getAllTasks();
    }
    @Operation(summary = "Get task", description = "Returns information about the specified task. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/tasks/{id}")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }
    @Operation(summary = "Get tasks by user", description = "Returns all tasks belonging to the specified user. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{userId}/tasks")
    public List<TaskResponse> getTasksByUserId(@PathVariable Long userId){
        return taskService.getTasksByUserId(userId);
    }
    @Operation(summary = "Create task", description = "Creates a new task for the specified user. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tasks")
    public TaskResponse createTask(@Valid @RequestBody AdminTaskCreateRequest request){
        return taskService.createTask(request);
    }
    @Operation(summary = "Update task", description = "Partially updates the specified task. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or no fields provided for update"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task or user not found")
    })
    @PatchMapping("/tasks/{taskId}")
    public TaskResponse updateTask(@PathVariable Long taskId, @Valid @RequestBody AdminUpdateTaskRequest request){
        return taskService.updateTask(taskId, request);
    }
    @Operation(summary = "Delete task", description = "Deletes the specified task. Accessible only to administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
