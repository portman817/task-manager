package task.manager.Task.Manager.dto.requests.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import task.manager.Task.Manager.enums.TaskStatus;

public class CurrentUserCreateTaskRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private TaskStatus taskStatus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
