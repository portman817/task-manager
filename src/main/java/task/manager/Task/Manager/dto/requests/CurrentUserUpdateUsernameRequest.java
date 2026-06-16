package task.manager.Task.Manager.dto.requests;

import jakarta.validation.constraints.NotBlank;

public class CurrentUserUpdateUsernameRequest {
    @NotBlank
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
