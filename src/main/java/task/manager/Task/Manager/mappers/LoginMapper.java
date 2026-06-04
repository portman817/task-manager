package task.manager.Task.Manager.mappers;


import task.manager.Task.Manager.dto.responses.LoginResponse;
import task.manager.Task.Manager.entity.User;


public class LoginMapper {
   public static LoginResponse toResponse(User user, String token){

        return new LoginResponse(
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                token);

    }
}
