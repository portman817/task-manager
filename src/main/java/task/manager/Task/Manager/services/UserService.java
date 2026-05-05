package task.manager.Task.Manager.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.repos.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> getAllUsers(){
        return (List<User>) userRepository.findAll();
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
    }
    public User createUser (User user){
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    public void deleteUser (Long id){
        User user = userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
            userRepository.delete(user);


    }
}
