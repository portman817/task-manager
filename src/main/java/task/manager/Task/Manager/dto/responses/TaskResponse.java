package task.manager.Task.Manager.dto.responses;

import task.manager.Task.Manager.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(Long taskId, String title, String description, TaskStatus status, LocalDateTime createdAt,
                           LocalDateTime updatedAt, UserResponse owner) {
}
