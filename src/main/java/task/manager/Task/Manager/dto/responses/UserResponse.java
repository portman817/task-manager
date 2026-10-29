package task.manager.Task.Manager.dto.responses;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import task.manager.Task.Manager.enums.Role;

public record UserResponse(Long id, String username, Role role,
                           @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
    public UserResponse(Long id, String username, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime createdAt() {
        return createdAt;
    }
}
