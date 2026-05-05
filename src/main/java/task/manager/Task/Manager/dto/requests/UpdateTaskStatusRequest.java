package task.manager.Task.Manager.dto.requests;

import task.manager.Task.Manager.enums.TaskStatus;

public class UpdateTaskStatusRequest {
    private TaskStatus taskStatus;

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
