package task.manager.Task.Manager.dto.responses;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import task.manager.Task.Manager.enums.Role;

public class UserResponse {
    private final Long id;
    private final String username;
    private final Role role;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String username, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.createdAt=createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public Role getRole(){return  role;}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
