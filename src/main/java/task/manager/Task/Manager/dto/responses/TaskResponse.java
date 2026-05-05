package task.manager.Task.Manager.dto.responses;

import task.manager.Task.Manager.enums.TaskStatus;

import java.time.LocalDateTime;

public class TaskResponse {
    private Long taskId;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse owner;

    public TaskResponse(Long taskId, String title, String description, TaskStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, UserResponse owner) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.owner = owner;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserResponse getOwner() {
        return owner;
    }
}
