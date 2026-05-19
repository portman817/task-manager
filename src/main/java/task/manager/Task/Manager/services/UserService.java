package task.manager.Task.Manager.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.dto.requests.UserCreateRequest;
import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.mappers.UserMapper;
import task.manager.Task.Manager.repos.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<UserResponse> getAllUsers(){
        Iterable<User> users = userRepository.findAll();
        List<UserResponse> result= new ArrayList<>();
        for (User user: users ){
            result.add(UserMapper.toResponse(user));
        }
        return result;
    }
    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        return UserMapper.toResponse(user);
    }
    public UserResponse createUser (UserCreateRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
    public void deleteUser (Long id){
        User user = userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
            userRepository.delete(user);


    }
}
