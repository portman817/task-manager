package task.manager.Task.Manager.dto.responses;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UserResponse {
    private final Long id;
    private final String username;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public UserResponse(Long id, String username, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt=createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
