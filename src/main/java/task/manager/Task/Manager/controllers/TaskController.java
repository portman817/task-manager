package task.manager.Task.Manager.controllers;


import org.springframework.web.bind.annotation.*;
import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.entity.Task;
import task.manager.Task.Manager.dto.requests.TaskCreateRequest;
import task.manager.Task.Manager.dto.requests.UpdateTaskStatusRequest;
import task.manager.Task.Manager.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping
    public List<TaskResponse> getAllTasks(){
        return taskService.getAllTasks();
    }
    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }
    @PostMapping
    public TaskResponse createTask(@RequestBody TaskCreateRequest request) {
        return taskService.createTask(request);
    }
    @PutMapping("/{id}/status")
    public TaskResponse updateTaskStatusByTaskId(@PathVariable Long id, @RequestBody UpdateTaskStatusRequest request){
        return taskService.updateTaskStatus(id, request.getTaskStatus());
    }
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
    }
}
