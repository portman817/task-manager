package task.manager.Task.Manager.dto.responses;

import task.manager.Task.Manager.enums.Role;

public class LoginResponse {
    private Long id;
    private String username;
    private Role role;
    private final String token;

    public LoginResponse(Long id, String username, Role role, String token) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}
