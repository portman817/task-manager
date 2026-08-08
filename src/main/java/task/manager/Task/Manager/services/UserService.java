package task.manager.Task.Manager.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.dto.requests.admin.AdminChangePasswordRequest;
import task.manager.Task.Manager.dto.requests.admin.AdminUserCreateRequest;
import task.manager.Task.Manager.dto.requests.admin.AdminUserUpdateRequest;
import task.manager.Task.Manager.dto.requests.auth.LoginRequest;
import task.manager.Task.Manager.dto.requests.auth.UserRegisterRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserChangePasswordRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserUpdateUsernameRequest;
import task.manager.Task.Manager.dto.responses.LoginResponse;
import task.manager.Task.Manager.dto.responses.UserResponse;
import task.manager.Task.Manager.entity.Task;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.enums.Role;
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
    public UserResponse createUser (AdminUserCreateRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exist");
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
        List<Task> tasks = taskRepository.findByOwnerUserId(id);
        if(!tasks.isEmpty()){
            taskRepository.deleteAll(tasks);
        }
        userRepository.delete(user);

    }
    public UserResponse updateUser(Long userId, AdminUserUpdateRequest request){
        boolean changed = false;
        if(request.getUsername()==null && request.getRole()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request body");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if(request.getUsername()!=null && !user.getUsername().equals(request.getUsername())){
            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent() && existingUser.get().getUserId() != userId){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already exist");
            }
            user.setUsername(request.getUsername());
            changed = true;
        }
        if(request.getRole()!=null && !user.getRole().equals(request.getRole())){
            user.setRole(request.getRole());
            changed = true;
        }
        if(!changed){
            return UserMapper.toResponse(user);
        }
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
    public void changePassword(Long id, AdminChangePasswordRequest request){
        User user = userRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the current password");
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
    public UserResponse getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return UserMapper.toResponse(user);
    }
    public UserResponse currentUserUpdateUsername(CurrentUserUpdateUsernameRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if(user.getUsername().equals(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New username must be different from the current username");
        }
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if(existingUser.isPresent() && existingUser.get().getUserId() != user.getUserId()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        user.setUsername(request.getUsername());
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
    public void currentUserChangePassword(CurrentUserChangePasswordRequest request){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean isPasswordCorrect= passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword());
        if(!isPasswordCorrect){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is incorrect");
        }
        if(request.getOldPassword().equals(request.getNewPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the current password");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
    public UserResponse register(UserRegisterRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exist");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

}
