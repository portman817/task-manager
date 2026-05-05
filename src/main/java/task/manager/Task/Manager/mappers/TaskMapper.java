package task.manager.Task.Manager.mappers;

import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.entity.Task;

public class TaskMapper {
    public static TaskResponse toResponse(Task task){
        return new TaskResponse(
                task.getTaskid(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                new UserResponse(
                        task.getOwner().getUserIdId(),
                        task.getOwner().getUsername()
                )

        );
    }
}
