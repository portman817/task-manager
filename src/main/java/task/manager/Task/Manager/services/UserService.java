package task.manager.Task.Manager.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.dto.requests.ChangePasswordRequest;
import task.manager.Task.Manager.dto.requests.LoginRequest;
import task.manager.Task.Manager.dto.requests.UserCreateRequest;
import task.manager.Task.Manager.dto.requests.UserUpdateRequest;
import task.manager.Task.Manager.dto.responses.LoginResponse;
import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.mappers.LoginMapper;
import task.manager.Task.Manager.mappers.UserMapper;
import task.manager.Task.Manager.repos.TaskRepository;
import task.manager.Task.Manager.repos.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskRepository = taskRepository;
        this.jwtService = jwtService;
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
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username alredy exist");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
    public void deleteUser (Long id){
        User user = userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));


        if (taskRepository.existsByOwner(user)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User has tasks and cannot be deleted"
            );
        }
        userRepository.delete(user);

    }
    public UserResponse updateUser(Long userId, UserUpdateRequest request){
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent() && existingUser.get().getUserId() != userId){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already exist");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
    public void changePassword(ChangePasswordRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtService.generateToken(user);
        return LoginMapper.toResponse(user, token);
    }
}
