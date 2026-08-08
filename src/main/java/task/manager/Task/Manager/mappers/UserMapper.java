package task.manager.Task.Manager.mappers;

import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.entity.User;

public class UserMapper {
    public static UserResponse toResponse(User user){
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
