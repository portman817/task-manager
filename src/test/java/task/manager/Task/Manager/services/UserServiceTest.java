package task.manager.Task.Manager.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import task.manager.Task.Manager.repos.TaskRepository;
import task.manager.Task.Manager.repos.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;
    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserByIdTests {

        @Test
        void shouldReturnUserWhenUserExists() {
            Long userId = 1L;
            LocalDateTime createdAt = LocalDateTime.now();
            User user = new User();
            user.setUserId(userId);
            user.setUsername("john");
            user.setCreatedAt(createdAt);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            UserResponse response = userService.getUserById(userId);
            assertEquals(userId, response.getId());
            assertEquals(user.getUsername(), response.getUsername());
            assertEquals(createdAt, response.getCreatedAt());
            verify(userRepository).findById(userId);
        }

        @Test
        void shouldThrowNotFoundWhenUserDoesNotExist() {
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.getUserById(userId));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(userRepository).findById(userId);
        }
    }
    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {

        @Test
        void shouldReturnAllUsers() {
            LocalDateTime createdAt1 = LocalDateTime.now();
            LocalDateTime createdAt2 = LocalDateTime.now().plusMinutes(1);
            User user1 = new User();
            user1.setUserId(1L);
            user1.setUsername("John");
            user1.setCreatedAt(createdAt1);
            User user2 = new User();
            user2.setUserId(2L);
            user2.setUsername("Anna");
            user2.setCreatedAt(createdAt2);
            when(userRepository.findAll()).thenReturn(List.of(user1, user2));
            List<UserResponse> result = userService.getAllUsers();
            assertEquals(2, result.size());
            assertEquals(user1.getUserId(), result.get(0).getId());
            assertEquals(user1.getUsername(), result.get(0).getUsername());
            assertEquals(createdAt1, result.get(0).getCreatedAt());
            assertEquals(user2.getUserId(), result.get(1).getId());
            assertEquals(user2.getUsername(), result.get(1).getUsername());
            assertEquals(createdAt2, result.get(1).getCreatedAt());
            verify(userRepository).findAll();
        }

        @Test
        void shouldReturnEmptyListWhenNoUsersExist() {
            when(userRepository.findAll()).thenReturn(List.of());
            List<UserResponse> result = userService.getAllUsers();
            assertTrue(result.isEmpty());
            verify(userRepository).findAll();
        }
    }
    @Nested
    @DisplayName("createUser()")
    class CreateUserTests {

        @Test
        void shouldCreateUserSuccessfully() {
            AdminUserCreateRequest request = new AdminUserCreateRequest();
            request.setUsername("John");
            request.setPassword("1234567");
            request.setRole(Role.USER);
            LocalDateTime createdAt = LocalDateTime.now();
            User savedUser = new User();
            savedUser.setUserId(1L);
            savedUser.setUsername("John");
            savedUser.setPassword("encodedPassword");
            savedUser.setRole(Role.USER);
            savedUser.setCreatedAt(createdAt);
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.getPassword())).thenReturn(savedUser.getPassword());
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            UserResponse response = userService.createUser(request);
            assertEquals(savedUser.getUserId(), response.getId());
            assertEquals(request.getUsername(), response.getUsername());
            assertEquals(createdAt, response.getCreatedAt());
            verify(userRepository).findByUsername(request.getUsername());
            verify(passwordEncoder).encode(request.getPassword());
            verify(userRepository).save(any(User.class));
        }

        @Test
        void shouldThrowConflictWhenUsernameAlreadyExists() {
            AdminUserCreateRequest request = new AdminUserCreateRequest();
            request.setUsername("John");
            request.setPassword("1234567");
            request.setRole(Role.USER);
            User user = new User();
            user.setUsername("John");
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createUser(request));
            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            assertEquals("Username already exist", exception.getReason());
            verify(userRepository).findByUsername(request.getUsername());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }
    @Nested
    @DisplayName("deleteUser()")
    class DeleteUserTests {

        @Test
        void shouldDeleteUserAndOwnedTasksSuccessfully() {
            User user = new User();
            user.setUserId(1L);
            Task task1 = new Task();
            task1.setOwner(user);
            Task task2 = new Task();
            task2.setOwner(user);
            List<Task> tasks = new ArrayList<>();
            tasks.add(task1);
            tasks.add(task2);
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(taskRepository.findByOwnerUserId(user.getUserId())).thenReturn(tasks);
            userService.deleteUser(user.getUserId());
            verify(userRepository).findById(user.getUserId());
            verify(taskRepository).findByOwnerUserId(user.getUserId());
            verify(taskRepository).deleteAll(tasks);
            verify(userRepository).delete(user);
        }

        @Test
        void shouldDeleteUserWhenUserHasNoTasks() {
            User user = new User();
            user.setUserId(1L);
            List<Task> tasks = List.of();
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(taskRepository.findByOwnerUserId(user.getUserId())).thenReturn(tasks);
            userService.deleteUser(user.getUserId());
            verify(userRepository).findById(user.getUserId());
            verify(taskRepository).findByOwnerUserId(user.getUserId());
            verify(taskRepository, never()).deleteAll(anyList());
            verify(userRepository).delete(user);
        }

        @Test
        void shouldNotDeleteAnythingWhenUserNotFound() {
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.deleteUser(userId));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(userRepository).findById(userId);
            verify(taskRepository, never()).findByOwnerUserId(userId);
            verify(taskRepository, never()).deleteAll(anyList());
            verify(userRepository, never()).delete(any(User.class));
        }
    }
    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        @Test
        void shouldThrowBadRequestWhenUsernameAndRoleAreNull() {
            AdminUserUpdateRequest request = new AdminUserUpdateRequest();
            Long userId = 1L;
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(userId, request));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Invalid request body", exception.getReason());
            verify(userRepository, never()).findById(userId);
            verify(userRepository, never()).findByUsername(anyString());
            verify(userRepository, never()).save(any(User.class));

        }

        @Test
        void shouldThrowNotFoundWhenUserIsNotFound() {
            AdminUserUpdateRequest request = new AdminUserUpdateRequest();
            request.setUsername("John");
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(userId, request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(userRepository).findById(userId);
            verify(userRepository, never()).findByUsername(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldUpdateUsernameSuccessfully() {
            AdminUserUpdateRequest request = new AdminUserUpdateRequest();
            request.setUsername("John");
            User user = new User();
            user.setUserId(1L);
            user.setUsername("Anna");
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(userRepository.save(user)).thenReturn(user);
            UserResponse response = userService.updateUser(user.getUserId(), request);
            assertEquals(user.getUserId(), response.getId());
            assertEquals(request.getUsername(), response.getUsername());
            verify(userRepository).findById(user.getUserId());
            verify(userRepository).findByUsername(request.getUsername());
            verify(userRepository).save(user);
        }

        @Test
        void shouldThrowConflictWhenNewUsernameAlreadyExists() {
            AdminUserUpdateRequest request = new AdminUserUpdateRequest();
            request.setUsername("John");
            User userToUpdate = new User();
            userToUpdate.setUserId(1L);
            userToUpdate.setUsername("Anna");
            User existingUser = new User();
            existingUser.setUserId(2L);
            existingUser.setUsername("John");
            when(userRepository.findById(userToUpdate.getUserId())).thenReturn(Optional.of(userToUpdate));
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(userToUpdate.getUserId(), request));
            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            assertEquals("Username is already exist", exception.getReason());
            verify(userRepository).findById(userToUpdate.getUserId());
            verify(userRepository).findByUsername(request.getUsername());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldUpdateRoleSuccessfully() {
            AdminUserUpdateRequest request = new AdminUserUpdateRequest();
            request.setRole(Role.USER);
            User user = new User();
            user.setUserId(1L);
            user.setRole(Role.ADMIN);
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            UserResponse response = userService.updateUser(user.getUserId(), request);
            assertEquals(user.getUserId(), response.getId());
            assertEquals(request.getRole(), response.getRole());
            verify(userRepository).findById(user.getUserId());
            verify(userRepository, never()).findByUsername(anyString());
            verify(userRepository).save(user);
        }

        @Test
        void shouldUpdateUsernameAndRoleSuccessfully() {
            AdminUserUpdateRequest request = new AdminUserUpdateRequest();
            request.setUsername("John");
            request.setRole(Role.ADMIN);
            User user = new User();
            user.setUsername("Anna");
            user.setRole(Role.USER);
            user.setUserId(1L);
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(userRepository.save(user)).thenReturn(user);
            UserResponse response = userService.updateUser(user.getUserId(), request);
            assertEquals(user.getUserId(), response.getId());
            assertEquals(request.getUsername(), response.getUsername());
            assertEquals(request.getRole(), response.getRole());
            verify(userRepository).findById(user.getUserId());
            verify(userRepository).findByUsername(request.getUsername());
            verify(userRepository).save(user);
        }

        @Test
        void shouldReturnCurrentUserWhenNothingChanged() {
            AdminUserUpdateRequest request = new AdminUserUpdateRequest();
            request.setRole(Role.USER);
            request.setUsername("John");
            User user = new User();
            user.setUserId(1L);
            user.setRole(Role.USER);
            user.setUsername("John");
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            UserResponse response = userService.updateUser(user.getUserId(), request);
            assertEquals(user.getUserId(), response.getId());
            assertEquals(request.getRole(), response.getRole());
            assertEquals(request.getUsername(), response.getUsername());
            verify(userRepository).findById(user.getUserId());
            verify(userRepository, never()).findByUsername(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }
    @Nested
    @DisplayName("changePassword()")
    class ChangePasswordTests {

        @Test
        void changePassword_ShouldThrowNotFoundWhenUserDoesNotExist() {
            AdminChangePasswordRequest request = new AdminChangePasswordRequest();
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.changePassword(userId, request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(userRepository).findById(userId);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void changePassword_ShouldThrowBadRequestWhenNewPasswordMatchesCurrentPassword() {
            AdminChangePasswordRequest request = new AdminChangePasswordRequest();
            request.setNewPassword("1234567");
            User user = new User();
            user.setUserId(1L);
            user.setPassword("encodedCurrentPassword");
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(true);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.changePassword(user.getUserId(), request));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("New password must be different from the current password", exception.getReason());
            verify(userRepository).findById(user.getUserId());
            verify(passwordEncoder).matches(request.getNewPassword(), user.getPassword());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void changePassword_ShouldChangePasswordSuccessfully() {
            AdminChangePasswordRequest request = new AdminChangePasswordRequest();
            request.setNewPassword("1234567");
            String encodedNewPassword = "encodedNewPassword";
            String encodedCurrentPassword = "encodedCurrentPassword";
            User user = new User();
            user.setUserId(1L);
            user.setPassword(encodedCurrentPassword);
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);
            when(passwordEncoder.encode(request.getNewPassword())).thenReturn(encodedNewPassword);
            userService.changePassword(user.getUserId(), request);
            assertEquals(encodedNewPassword, user.getPassword());
            verify(userRepository).findById(user.getUserId());
            verify(passwordEncoder).matches(request.getNewPassword(), encodedCurrentPassword);
            verify(passwordEncoder).encode(request.getNewPassword());
            verify(userRepository).save(user);

        }
    }
    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        void login_ShouldThrowUnauthorizedWhenUserDoesNotExist() {
            LoginRequest request = new LoginRequest();
            request.setUsername("John");
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.login(request));
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
            assertEquals("Invalid credentials", exception.getReason());
            verify(userRepository).findByUsername(request.getUsername());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateToken(any(User.class));
        }

        @Test
        void login_ShouldThrowUnauthorizedWhenPasswordIsInvalid() {
            LoginRequest request = new LoginRequest();
            String name = "John";
            request.setUsername(name);
            request.setPassword("1234567");
            User user = new User();
            user.setUsername(name);
            user.setPassword("encodedPassword");
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.login(request));
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
            assertEquals("Invalid credentials", exception.getReason());
            verify(userRepository).findByUsername(request.getUsername());
            verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
            verify(jwtService, never()).generateToken(any(User.class));
        }

        @Test
        void login_ShouldReturnLoginResponseSuccessfully() {
            String name = "John";
            LoginRequest request = new LoginRequest();
            request.setUsername(name);
            request.setPassword("1234567");
            String token = "token";
            User user = new User();
            user.setUsername(name);
            user.setPassword("encodedPassword");
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(user)).thenReturn(token);
            LoginResponse response = userService.login(request);
            assertEquals(user.getUsername(), response.getUsername());
            assertEquals(token, response.getToken());
            verify(userRepository).findByUsername(request.getUsername());
            verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
            verify(jwtService).generateToken(user);
        }
    }
    @Nested
    @DisplayName("getCurrentUser()")
    class GetCurrentUserTests {

        @Test
        void getCurrentUser_ShouldReturnCurrentUserSuccessfully() {
            String name = "John";
            User user = new User();
            user.setUsername(name);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(name);
            when(userRepository.findByUsername(name)).thenReturn(Optional.of(user));
            SecurityContextHolder.setContext(securityContext);
            UserResponse response = userService.getCurrentUser();
            assertEquals(user.getUsername(), response.getUsername());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(name);


        /*String name = "John";
        Authentication authentication = new UsernamePasswordAuthenticationToken(name, null);
        User user = new User();
        user.setUsername(name);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(user));       <--- Zweite Variante von Test
        UserResponse response = userService.getCurrentUser();
        assertEquals(authentication.getName(), response.getUsername());
        verify(userRepository).findByUsername(authentication.getName());*/
        }

        @Test
        void getCurrentUser_ShouldThrowNotFoundWhenAuthenticatedUserDoesNotExist() {
            String name = "John";
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(name);
            when(userRepository.findByUsername(name)).thenReturn(Optional.empty());
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.getCurrentUser());
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(name);
        }
    }
    @Nested
    @DisplayName("currentUserUpdateUsername()")
    class CurrentUserUpdateUsernameTests {

        @Test
        void currentUserUpdateUsername_ShouldUpdateUsernameSuccessfully() {
            String currentUsername = "Anna";
            String newUsername = "John";
            long userId = 1L;
            CurrentUserUpdateUsernameRequest request = new CurrentUserUpdateUsernameRequest();
            request.setUsername(newUsername);
            User user = new User();
            user.setUsername(currentUsername);
            user.setUserId(userId);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(currentUsername);
            when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
            when(userRepository.findByUsername(newUsername)).thenReturn(Optional.empty());
            when(userRepository.save(user)).thenReturn(user);
            SecurityContextHolder.setContext(securityContext);
            UserResponse response = userService.currentUserUpdateUsername(request);
            assertEquals(user.getUserId(), response.getId());
            assertEquals(request.getUsername(), response.getUsername());
            assertEquals(newUsername, user.getUsername());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(currentUsername);
            verify(userRepository).findByUsername(newUsername);
            verify(userRepository).save(user);
        }

        @Test
        void currentUserUpdateUsername_ShouldThrowNotFoundWhenAuthenticatedUserDoesNotExist() {
            String newUsername = "John";
            String currentUsername = "Anna";
            CurrentUserUpdateUsernameRequest request = new CurrentUserUpdateUsernameRequest();
            request.setUsername(newUsername);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(currentUsername);
            when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.empty());
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.currentUserUpdateUsername(request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(currentUsername);
            verify(userRepository, never()).findByUsername(newUsername);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void currentUserUpdateUsername_ShouldThrowConflictWhenUsernameAlreadyExists() {
            String newUsername = "John";
            String currentUsername = "Anna";
            long userId = 1l;
            long existingUserId = 2L;
            CurrentUserUpdateUsernameRequest request = new CurrentUserUpdateUsernameRequest();
            request.setUsername(newUsername);
            User user = new User();
            user.setUserId(userId);
            user.setUsername(currentUsername);
            User existingUser = new User();
            existingUser.setUserId(existingUserId);
            existingUser.setUsername(newUsername);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(currentUsername);
            when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.currentUserUpdateUsername(request));
            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            assertEquals("Username already exist", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(currentUsername);
            verify(userRepository).findByUsername(request.getUsername());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void currentUserUpdateUsername_ShouldThrowBadRequestWhenNewUsernameMatchesCurrentUsername() {
            String newUsername = "Anna";
            String currentUsername = "Anna";
            CurrentUserUpdateUsernameRequest request = new CurrentUserUpdateUsernameRequest();
            request.setUsername(newUsername);
            User user = new User();
            user.setUsername(currentUsername);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(currentUsername);
            when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.currentUserUpdateUsername(request));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("New username must be different from the current username", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(currentUsername);
            verify(userRepository, never()).save(any(User.class));
        }
    }
    @Nested
    @DisplayName("currentUserChangePassword()")
    class CurrentUserChangePasswordTests {

        @Test
        void currentUserChangePassword_ShouldThrowNotFoundWhenAuthenticatedUserDoesNotExist() {
            CurrentUserChangePasswordRequest request = new CurrentUserChangePasswordRequest();
            String username = "Anna";
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.currentUserChangePassword(request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void currentUserChangePassword_ShouldThrowUnauthorizedWhenOldPasswordIsIncorrect() {
            String username = "Anna";
            String oldPassword = "oldPassword";
            String encodedOldPassword = "encodedOldPassword";
            CurrentUserChangePasswordRequest request = new CurrentUserChangePasswordRequest();
            request.setOldPassword(oldPassword);
            User user = new User();
            user.setPassword(encodedOldPassword);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getOldPassword(), encodedOldPassword)).thenReturn(false);
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.currentUserChangePassword(request));
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
            assertEquals("Old password is incorrect", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(passwordEncoder).matches(request.getOldPassword(), encodedOldPassword);
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));

        }

        @Test
        void currentUserChangePassword_ShouldThrowBadRequestWhenNewPasswordMatchesOldPassword() {
            String username = "Anna";
            String oldPassword = "password";
            String newPassword = "password";
            String encodedOldPassword = "encodedOldPassword";
            CurrentUserChangePasswordRequest request = new CurrentUserChangePasswordRequest();
            request.setOldPassword(oldPassword);
            request.setNewPassword(newPassword);
            User user = new User();
            user.setPassword(encodedOldPassword);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getOldPassword(), encodedOldPassword)).thenReturn(true);
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.currentUserChangePassword(request));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("New password must be different from the current password", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(passwordEncoder).matches(request.getOldPassword(), encodedOldPassword);
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void currentUserChangePassword_ShouldChangePasswordSuccessfully() {
            String username = "Anna";
            String oldPassword = "oldPassword";
            String newPassword = "newPassword";
            String encodedOldPassword = "encodedOldPassword";
            String encodedNewPassword = "encodedNewPassword";
            CurrentUserChangePasswordRequest request = new CurrentUserChangePasswordRequest();
            request.setOldPassword(oldPassword);
            request.setNewPassword(newPassword);
            User user = new User();
            user.setPassword(encodedOldPassword);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getOldPassword(), encodedOldPassword)).thenReturn(true);
            when(passwordEncoder.encode(request.getNewPassword())).thenReturn(encodedNewPassword);
            SecurityContextHolder.setContext(securityContext);
            userService.currentUserChangePassword(request);
            assertEquals(encodedNewPassword, user.getPassword());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(passwordEncoder).matches(request.getOldPassword(), encodedOldPassword);
            verify(passwordEncoder).encode(request.getNewPassword());
            verify(userRepository).save(user);
        }
    }
    @Nested
    @DisplayName("register()")
    class RegisterTests{
        @Test
        void register_ShouldThrowConflictWhenUsernameAlreadyExists(){
            String username = "John";
            UserRegisterRequest request = new UserRegisterRequest();
            request.setUsername(username);
            User user = new User();
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> userService.register(request));
            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            assertEquals("Username already exist", exception.getReason());
            verify(userRepository).findByUsername(request.getUsername());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
        @Test
        void register_ShouldRegisterUserSuccessfully(){
            String username = "John";
            String password = "1234567";
            String encodedPassword = "encodedPassword";
            UserRegisterRequest request = new UserRegisterRequest();
            request.setUsername(username);
            request.setPassword(password);
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            UserResponse response = userService.register(request);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).findByUsername(request.getUsername());
            verify(passwordEncoder).encode(request.getPassword());
            verify(userRepository).save(captor.capture());
            User capturedUser = captor.getValue();
            assertEquals(request.getUsername(), capturedUser.getUsername());
            assertEquals(encodedPassword, capturedUser.getPassword());
            assertEquals(Role.USER, capturedUser.getRole());
            assertNotNull(capturedUser.getCreatedAt());
            assertEquals(request.getUsername(), response.getUsername());
            assertEquals(Role.USER, response.getRole());
        }
    }

}
