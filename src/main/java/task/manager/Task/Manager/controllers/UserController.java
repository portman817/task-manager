package task.manager.Task.Manager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Current User", description = "Operations available for authenticated users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {

        this.userService = userService;
        this.taskService=taskService;
    }

    @Operation(summary = "Get tasks by current user", description = "Returns all tasks belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns a list of tasks owned by the authenticated user. The list may be empty."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/tasks")
    public List<TaskResponse> getTasksByCurrentUser(){
        return taskService.getTasksByCurrentUser();
    }
    @Operation(summary = "Create new Task", description = "Creates a new task for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")

    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tasks")
    public TaskResponse createTaskByCurrentUser(@Valid @RequestBody CurrentUserCreateTaskRequest request){
        return taskService.createTaskByCurrentUser(request);
    }
    @Operation(summary = "Get current user", description = "Returns information about the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public UserResponse getCurrentUser(){
        return userService.getCurrentUser();
    }
    @Operation(summary = "Update current username", description = "Updates username of the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Username updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PatchMapping
    public UserResponse currentUserUpdateUsername(@Valid @RequestBody CurrentUserUpdateUsernameRequest request){
        return userService.currentUserUpdateUsername(request);
    }
    @Operation(summary = "Update password by current user", description = "Update password of the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or new password must be different from the current password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or old password is incorrect"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/password")
    public ResponseEntity<Void> currentUserChangePassword(@Valid @RequestBody CurrentUserChangePasswordRequest request){
         userService.currentUserChangePassword(request);
         return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Update task", description = "Partially updates the specified task if it belongs to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or no fields provided for update"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Task does not belong to the authenticated user"),
            @ApiResponse(responseCode = "404", description = "Task or authenticated user not found")
    })
    @PatchMapping("/tasks/{id}")
    public TaskResponse updateTaskByCurrentUser(@PathVariable Long id, @Valid @RequestBody CurrentUserUpdateTaskRequest request){
        return taskService.currentUserUpdateTask(id, request);
    }
    @Operation(summary = "Delete task", description = "Deletes the specified task if it belongs to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Task does not belong to the authenticated user"),
            @ApiResponse(responseCode = "404", description = "Task or authenticated user not found")
    })
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> currentUserDeleteTask(@PathVariable Long id){
        taskService.deleteTaskByCurrentUser(id);
        return ResponseEntity.noContent().build();
    }
}
