package task.manager.Task.Manager.entity;

import jakarta.persistence.*;
import task.manager.Task.Manager.enums.TaskStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskid;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    private LocalDateTime  createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name= "userid")
    private User owner;

    public Long getTaskid() {
        return taskid;
    }

    public void setTaskid(Long id) {
        this.taskid = id;
    }

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
