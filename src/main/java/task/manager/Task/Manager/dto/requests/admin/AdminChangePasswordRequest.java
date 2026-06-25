package task.manager.Task.Manager.dto.requests.admin;

import jakarta.validation.constraints.NotBlank;

public class AdminChangePasswordRequest {

    @NotBlank
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}


