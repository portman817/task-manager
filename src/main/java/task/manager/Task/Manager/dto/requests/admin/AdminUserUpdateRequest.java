package task.manager.Task.Manager.dto.requests.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import task.manager.Task.Manager.enums.Role;

public class AdminUserUpdateRequest {
    @NotBlank
    private String username;
    @NotNull
    private Role role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
