package task.manager.Task.Manager.dto.responses;

import task.manager.Task.Manager.enums.Role;

public record LoginResponse(Long id, String username, Role role, String token) {
}
