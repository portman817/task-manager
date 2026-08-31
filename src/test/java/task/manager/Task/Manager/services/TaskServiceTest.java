package task.manager.Task.Manager.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import task.manager.Task.Manager.dto.requests.admin.AdminTaskCreateRequest;
import task.manager.Task.Manager.dto.requests.admin.AdminUpdateTaskRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserCreateTaskRequest;
import task.manager.Task.Manager.dto.requests.user.CurrentUserUpdateTaskRequest;
import task.manager.Task.Manager.dto.responses.TaskResponse;
import task.manager.Task.Manager.entity.Task;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.enums.Role;
import task.manager.Task.Manager.enums.TaskStatus;
import task.manager.Task.Manager.repos.TaskRepository;
import task.manager.Task.Manager.repos.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
   private User user;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;
    @InjectMocks
    private TaskService taskService;
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
    @BeforeEach
    void Setup(){
        user = new User();
        user.setUserId(3L);
        user.setUsername("Anna");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
    }
    @Nested
    @DisplayName("getAllTasks()")
    class GetAllTasksTests{
        @Test
        void getAllTasks_ShouldReturnAllTasksSuccessfully(){
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime updatedAt = createdAt.plusMinutes(1);
            Task task1 = new Task();
            task1.setDescription("Description1");
            task1.setTitle("Title1");
            task1.setTaskid(1L);
            task1.setStatus(TaskStatus.WARTET);
            task1.setOwner(user);
            task1.setCreatedAt(createdAt);
            task1.setUpdatedAt(updatedAt);
            Task task2 = new Task();
            task2.setDescription("Description2");
            task2.setTitle("Title2");
            task2.setTaskid(2L);
            task2.setStatus(TaskStatus.WARTET);
            task2.setOwner(user);
            task2.setCreatedAt(createdAt);
            task2.setUpdatedAt(updatedAt);
            when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
            List<TaskResponse> responses = taskService.getAllTasks();
            assertEquals(2, responses.size());
            assertEquals(task1.getTitle(), responses.get(0).getTitle());
            assertEquals(task2.getTitle(), responses.get(1).getTitle());
            assertEquals(task1.getTaskid(), responses.get(0).getTaskId());
            assertEquals(task2.getTaskid(), responses.get(1).getTaskId());
            assertEquals(task1.getOwner().getUserId(), responses.get(0).getOwner().getId());
            assertEquals(task2.getOwner().getUserId(), responses.get(1).getOwner().getId());
            verify(taskRepository).findAll();
        }
        @Test
        void getAllTasks_ShouldReturnEmptyListWhenNoTasksExist(){
            when(taskRepository.findAll()).thenReturn(List.of());
            List<TaskResponse>responses = taskService.getAllTasks();
            assertTrue(responses.isEmpty());
            verify(taskRepository).findAll();
        }

    }
    @Nested
    @DisplayName("getTaskById()")
    class GetTaskByIdTests{
        @Test
        void getTaskById_ShouldReturnTaskSuccessfully(){
            long taskId = 1L;
            Task task = new Task();
            task.setTaskid(taskId);
            task.setOwner(user);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            TaskResponse response = taskService.getTaskById(taskId);
            assertEquals(taskId, response.getTaskId());
            assertEquals(user.getUserId(), response.getOwner().getId());
            verify(taskRepository).findById(taskId);
        }
        @Test
        void getTaskById_ShouldThrowRuntimeExceptionWhenTaskDoesNotExist(){
            long taskId = 1L;
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
            RuntimeException exception = assertThrows(RuntimeException.class, ()->taskService.getTaskById(taskId));
            assertEquals("Task not found", exception.getMessage());
            verify(taskRepository).findById(taskId);
        }
    }
    @Nested
    @DisplayName("getTasksByUserId()")
    class GetTasksByUserIdTests{
        @Test
        void getTasksByUserId_ShouldReturnTasksByUserIdSuccessfully(){
            Task task1 = new Task();
            task1.setTaskid(1L);
            task1.setOwner(user);
            Task task2 = new Task();
            task2.setTaskid(2L);
            task2.setOwner(user);
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(taskRepository.findByOwnerUserId(user.getUserId())).thenReturn(List.of(task1, task2));
            List<TaskResponse>responses = taskService.getTasksByUserId(user.getUserId());
            assertEquals(2, responses.size());
            assertEquals(task1.getTaskid(), responses.get(0).getTaskId());
            assertEquals(task1.getOwner().getUserId(), responses.get(0).getOwner().getId());
            assertEquals(task2.getTaskid(), responses.get(1).getTaskId());
            assertEquals(task2.getOwner().getUserId(), responses.get(1).getOwner().getId());
            verify(userRepository).findById(user.getUserId());
            verify(taskRepository).findByOwnerUserId(user.getUserId());
        }
        @Test
        void getTasksByUserId_ShouldThrowNotFoundWhenUserDoesNotExist(){
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.getTasksByUserId(user.getUserId()));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(userRepository).findById(user.getUserId());
            verify(taskRepository, never()).findByOwnerUserId(anyLong());
        }
        @Test
        void getTasksByUserId_ShouldReturnEmptyListWhenUserHasNoTasks(){
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(taskRepository.findByOwnerUserId(user.getUserId())).thenReturn(List.of());
            List<TaskResponse>responses = taskService.getTasksByUserId(user.getUserId());
            assertTrue(responses.isEmpty());
            verify(userRepository).findById(user.getUserId());
            verify(taskRepository).findByOwnerUserId(user.getUserId());
        }
    }
    @Nested
    @DisplayName("createTask()")
    class CreateTaskTests{
        @Test
        void createTask_ShouldCreateTaskSuccessfully(){
            AdminTaskCreateRequest request = new AdminTaskCreateRequest();
            request.setUserId(user.getUserId());
            request.setTaskStatus(TaskStatus.IN_BEARBEITUNG);
            request.setTitle("TaskTitle");
            request.setDescription("Description");
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation ->invocation.getArgument(0));
            TaskResponse response = taskService.createTask(request);
            ArgumentCaptor<Task>captor = ArgumentCaptor.forClass(Task.class);
            verify(userRepository).findById(user.getUserId());
            verify(taskRepository).save(captor.capture());
            Task capturedTask = captor.getValue();
            assertEquals(request.getTitle(), capturedTask.getTitle());
            assertEquals(user.getUserId(), capturedTask.getOwner().getUserId());
            assertEquals(request.getTaskStatus(), capturedTask.getStatus());
            assertEquals(request.getDescription(), capturedTask.getDescription());
            assertNotNull(capturedTask.getCreatedAt());
            assertNotNull(capturedTask.getUpdatedAt());
            assertEquals(user.getUserId(), response.getOwner().getId());
            assertEquals(request.getTitle(), response.getTitle());
            assertEquals(request.getTaskStatus(), response.getStatus());
            assertEquals(request.getDescription(), response.getDescription());
        }
        @Test
        void createTask_ShouldThrowNotFoundWhenUserDoesNotExist(){
            AdminTaskCreateRequest request = new AdminTaskCreateRequest();
            request.setUserId(user.getUserId());
            request.setTaskStatus(TaskStatus.IN_BEARBEITUNG);
            request.setTitle("TaskTitle");
            request.setDescription("Description");
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.createTask(request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(userRepository).findById(user.getUserId());
            verify(taskRepository, never()).save(any(Task.class));
        }
    }
   @Nested
   @DisplayName("updateTask()")
   class UpdateTaskTests{
       @Test
       void updateTask_ShouldThrowNotFoundWhenTaskDoesNotExist(){
           long taskId = 1L;
           AdminUpdateTaskRequest request = new AdminUpdateTaskRequest();
           when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
           ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.updateTask(taskId, request));
           assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
           assertEquals("Task not found", exception.getReason());
           verify(taskRepository).findById(taskId);
           verify(userRepository, never()).findById(anyLong());
           verify(taskRepository, never()).save(any(Task.class));
       }
       @Test
       void updateTask_ShouldThrowBadRequestWhenNoFieldsAreProvided(){
           long taskId = 1L;
           AdminUpdateTaskRequest request = new AdminUpdateTaskRequest();
           Task task = new Task();
           when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
           ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.updateTask(taskId, request));
           assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
           assertEquals("At least one field must be provided", exception.getReason());
           verify(taskRepository).findById(taskId);
           verify(userRepository, never()).findById(anyLong());
           verify(taskRepository, never()).save(any(Task.class));
       }
       @Test
       void updateTask_ShouldUpdateTaskOwnerSuccessfully(){
           long taskId = 1L;
           AdminUpdateTaskRequest request = new AdminUpdateTaskRequest();
           request.setUserId(user.getUserId());
           User currentOwner = new User();
           currentOwner.setUserId(2L);
           Task task = new Task();
           task.setDescription("Description");
           task.setTitle("Title");
           task.setTaskid(taskId);
           task.setStatus(TaskStatus.WARTET);
           task.setOwner(currentOwner);
           when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
           when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
           when(taskRepository.save(task)).thenReturn(task);
           TaskResponse response = taskService.updateTask(taskId, request);
           assertEquals(request.getUserId(), task.getOwner().getUserId());
           assertEquals(user.getUserId(), response.getOwner().getId());
           verify(taskRepository).findById(taskId);
           verify(userRepository).findById(request.getUserId());
           verify(taskRepository).save(task);
       }
       @Test
       void updateTask_ShouldThrowNotFoundWhenNewOwnerDoesNotExist(){
           long taskId = 1L;
           AdminUpdateTaskRequest request = new AdminUpdateTaskRequest();
           request.setUserId(user.getUserId());
           User currentOwner = new User();
           currentOwner.setUserId(2L);
           Task task = new Task();
           task.setTaskid(taskId);
           task.setOwner(currentOwner);
           when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
           when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());
           ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.updateTask(taskId, request));
           assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
           assertEquals("User not found", exception.getReason());
           verify(taskRepository).findById(taskId);
           verify(userRepository).findById(request.getUserId());
           verify(taskRepository, never()).save(any(Task.class));
       }
       @Test
       void updateTask_ShouldUpdateTaskFieldsSuccessfully(){
           long taskId = 1L;
           AdminUpdateTaskRequest request = new AdminUpdateTaskRequest();
           request.setDescription("Update description");
           request.setTaskStatus(TaskStatus.IN_BEARBEITUNG);
           request.setTitle("Update title");
           Task task = new Task();
           task.setDescription("Description");
           task.setTitle("Title");
           task.setTaskid(1L);
           task.setOwner(user);
           task.setStatus(TaskStatus.WARTET);
           when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
           when(taskRepository.save(task)).thenReturn(task);
           TaskResponse response = taskService.updateTask(taskId, request);
           assertEquals(request.getTaskStatus(), task.getStatus());
           assertEquals(request.getTitle(), task.getTitle());
           assertEquals(request.getDescription(), task.getDescription());
           assertNotNull(task.getUpdatedAt());
           assertEquals(request.getTaskStatus(), response.getStatus());
           assertEquals(request.getTitle(), response.getTitle());
           assertEquals(request.getDescription(), response.getDescription());
           verify(taskRepository).findById(taskId);
           verify(userRepository, never()).findById(anyLong());
           verify(taskRepository).save(task);
       }
       @Test
       void updateTask_ShouldNotChangeOwnerWhenUserIdMatchesCurrentOwner(){
           long taskId = 1L;
           AdminUpdateTaskRequest request = new AdminUpdateTaskRequest();
           request.setUserId(user.getUserId());
           Task task = new Task();
           task.setOwner(user);
           task.setTaskid(taskId);
           when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
           TaskResponse response = taskService.updateTask(taskId, request);
           assertEquals(user.getUserId(), task.getOwner().getUserId());
           assertEquals(user.getUserId(), response.getOwner().getId());
           verify(taskRepository).findById(taskId);
           verify(userRepository, never()).findById(anyLong());
           verify(taskRepository, never()).save(any(Task.class));
       }
       @Test
       void updateTask_ShouldReturnCurrentTaskWhenNothingChanged(){
           long taskId = 1L;
           String title = "Title";
           TaskStatus status = TaskStatus.WARTET;
           String description = "Description";
           Task task = new Task();
           task.setTitle(title);
           task.setTaskid(taskId);
           task.setDescription(description);
           task.setStatus(status);
           task.setOwner(user);
           AdminUpdateTaskRequest request = new AdminUpdateTaskRequest();
           request.setUserId(user.getUserId());
           request.setTitle(title);
           request.setDescription(description);
           request.setTaskStatus(status);
           when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
           TaskResponse response = taskService.updateTask(taskId, request);
           assertEquals(request.getUserId(), task.getOwner().getUserId());
           assertEquals(request.getTitle(), task.getTitle());
           assertEquals(request.getDescription(), task.getDescription());
           assertEquals(request.getTaskStatus(), task.getStatus());
           assertEquals(request.getUserId(), response.getOwner().getId());
           assertEquals(request.getTitle(), response.getTitle());
           assertEquals(request.getDescription(), response.getDescription());
           assertEquals(request.getTaskStatus(), response.getStatus());
           verify(taskRepository).findById(taskId);
           verify(userRepository, never()).findById(anyLong());
           verify(taskRepository, never()).save(any(Task.class));
       }
   }
    @Nested
    @DisplayName("currentUserUpdateTask()")
    class CurrentUserUpdateTaskTests{
        @Test
        void currentUserUpdateTask_ShouldThrowNotFoundWhenTaskDoesNotExist(){
            long taskId = 1L;
            CurrentUserUpdateTaskRequest request = new CurrentUserUpdateTaskRequest();
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.currentUserUpdateTask(taskId, request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Task not found", exception.getReason());
            verify(taskRepository).findById(taskId);
            verify(securityContext, never()).getAuthentication();
            verify(authentication, never()).getName();
            verify(userRepository, never()).findByUsername(anyString());
            verify(taskRepository, never()).save(any(Task.class));
        }
        @Test
        void currentUserUpdateTask_ShouldThrowNotFoundWhenCurrentUserDoesNotExist(){
            long taskId = 1L;
            String username = "John";
            CurrentUserUpdateTaskRequest request = new CurrentUserUpdateTaskRequest();
            request.setTitle("Title");
            request.setTaskStatus(TaskStatus.WARTET);
            request.setDescription("Description");
            Task task = new Task();
            task.setTaskid(taskId);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.currentUserUpdateTask(taskId, request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).save(any(Task.class));
        }
        @Test
        void currentUserUpdateTask_ShouldThrowForbiddenWhenTaskBelongsToAnotherUser(){
            long taskId = 1L;
            String username = "Anna";
            User taskOwner = new User();
            taskOwner.setUserId(4L);
            taskOwner.setUsername("John");
            taskOwner.setRole(Role.USER);
            taskOwner.setCreatedAt(LocalDateTime.now());
            CurrentUserUpdateTaskRequest request = new CurrentUserUpdateTaskRequest();
            Task task = new Task();
            task.setTaskid(taskId);
            task.setOwner(taskOwner);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.currentUserUpdateTask(taskId, request));
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).save(any(Task.class));
        }
        @Test
        void currentUserUpdateTask_ShouldThrowBadRequestWhenNoFieldsAreProvided(){
            long taskId = 1L;
            String username = "Anna";
            CurrentUserUpdateTaskRequest request = new CurrentUserUpdateTaskRequest();
            Task task = new Task();
            task.setTaskid(taskId);
            task.setOwner(user);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.currentUserUpdateTask(taskId, request));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("At least one field must be provided", exception.getReason());
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).save(any(Task.class));
        }
        @Test
        void currentUserUpdateTask_ShouldUpdateTaskSuccessfully(){
            long taskId = 1L;
            String username = "Anna";
            CurrentUserUpdateTaskRequest request = new CurrentUserUpdateTaskRequest();
            request.setTitle("Title");
            request.setDescription("Description");
            request.setTaskStatus(TaskStatus.WARTET);
            Task task = new Task();
            task.setTaskid(taskId);
            task.setOwner(user);
            task.setTitle("CurrentTitle");
            task.setDescription("CurrentDescription");
            task.setStatus(TaskStatus.IN_BEARBEITUNG);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(taskRepository.save(task)).thenReturn(task);
            SecurityContextHolder.setContext(securityContext);
            TaskResponse response = taskService.currentUserUpdateTask(taskId, request);
            assertEquals(request.getTitle(), task.getTitle());
            assertEquals(request.getDescription(), task.getDescription());
            assertEquals(request.getTaskStatus(), task.getStatus());
            assertNotNull(task.getUpdatedAt());
            assertEquals(request.getTitle(), response.getTitle());
            assertEquals(request.getDescription(), response.getDescription());
            assertEquals(request.getTaskStatus(), response.getStatus());
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository).save(task);
        }
        @Test
        void currentUserUpdateTask_ShouldReturnCurrentTaskWhenNothingChanged(){
            long taskId = 1L;
            String username = "Anna";
            CurrentUserUpdateTaskRequest request = new CurrentUserUpdateTaskRequest();
            String title = "Title";
            String description = "Description";
            TaskStatus status = TaskStatus.WARTET;
            request.setTitle(title);
            request.setDescription(description);
            request.setTaskStatus(status);
            Task task = new Task();
            task.setTaskid(taskId);
            task.setOwner(user);
            task.setTitle(title);
            task.setDescription(description);
            task.setStatus(status);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            SecurityContextHolder.setContext(securityContext);
            TaskResponse response = taskService.currentUserUpdateTask(taskId, request);
            assertEquals(request.getTitle(), response.getTitle());
            assertEquals(request.getDescription(), response.getDescription());
            assertEquals(request.getTaskStatus(), response.getStatus());
            assertNull(task.getUpdatedAt());
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).save(any(Task.class));
        }
    }
   @Nested
   @DisplayName("deleteTask()")
   class DeleteTaskTests{
       @Test
       void deleteTask_ShouldDeleteTaskSuccessfully(){
           long taskId = 1L;
           Task task = new Task();
           task.setTaskid(taskId);
           when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
           taskService.deleteTask(taskId);
           verify(taskRepository).findById(taskId);
           verify(taskRepository).deleteById(taskId);
       }
       @Test
       void deleteTask_ShouldThrowNotFoundWhenTaskDoesNotExist(){
           long taskId = 1L;
           when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
           ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.deleteTask(taskId));
           assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
           assertEquals("Task not found", exception.getReason());
           verify(taskRepository).findById(taskId);
           verify(taskRepository, never()).deleteById(taskId);
       }
   }

    @Nested
    @DisplayName("getTaskByCurrentUser()")
    class GetTaskByCurrentUserTests{
        @Test
        void getTasksByCurrentUser_ShouldReturnCurrentUserTasksSuccessfully(){
            String username = "Anna";
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime updatedAt = createdAt.plusMinutes(1);
            Task task1 = new Task();
            task1.setDescription("Description1");
            task1.setTitle("Title1");
            task1.setTaskid(1L);
            task1.setStatus(TaskStatus.WARTET);
            task1.setOwner(user);
            task1.setCreatedAt(createdAt);
            task1.setUpdatedAt(updatedAt);
            Task task2 = new Task();
            task2.setDescription("Description2");
            task2.setTitle("Title2");
            task2.setTaskid(2L);
            task2.setStatus(TaskStatus.WARTET);
            task2.setOwner(user);
            task2.setCreatedAt(createdAt);
            task2.setUpdatedAt(updatedAt);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(taskRepository.findByOwnerUserId(user.getUserId())).thenReturn(List.of(task1, task2));
            SecurityContextHolder.setContext(securityContext);
            List<TaskResponse>responses = taskService.getTasksByCurrentUser();
            assertEquals(task1.getTaskid(), responses.get(0).getTaskId());
            assertEquals(task1.getTitle(), responses.get(0).getTitle());
            assertEquals(task1.getOwner().getUserId(), responses.get(0).getOwner().getId());
            assertEquals(task2.getTaskid(), responses.get(1).getTaskId());
            assertEquals(task2.getTitle(), responses.get(1).getTitle());
            assertEquals(task2.getOwner().getUserId(), responses.get(1).getOwner().getId());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository).findByOwnerUserId(user.getUserId());
        }
        @Test
        void getTasksByCurrentUser_ShouldThrowNotFoundWhenCurrentUserDoesNotExist(){
            String username = "Anna";
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.getTasksByCurrentUser());
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).findByOwnerUserId(anyLong());
        }
        @Test
        void getTasksByCurrentUser_ShouldReturnEmptyListWhenCurrentUserHasNoTasks(){
            String username = "Anna";
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(taskRepository.findByOwnerUserId(user.getUserId())).thenReturn(List.of());
            SecurityContextHolder.setContext(securityContext);
            List<TaskResponse> responses = taskService.getTasksByCurrentUser();
            assertTrue(responses.isEmpty());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository).findByOwnerUserId(user.getUserId());
        }
    }
    @Nested
    @DisplayName("createTaskByCurrentUser()")
    class CreateTaskByCurrentUserTests{
        @Test
        void createTaskByCurrentUser_ShouldCreateTaskSuccessfully(){
            String username = "Anna";
            CurrentUserCreateTaskRequest request = new CurrentUserCreateTaskRequest();
            request.setTitle("Title");
            request.setDescription("Description");
            request.setTaskStatus(TaskStatus.WARTET);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation ->invocation.getArgument(0));
            SecurityContextHolder.setContext(securityContext);
            TaskResponse response = taskService.createTaskByCurrentUser(request);
            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository).save(captor.capture());
            Task capturedTask = captor.getValue();
            assertEquals(request.getTitle(), capturedTask.getTitle());
            assertEquals(request.getDescription(), capturedTask.getDescription());
            assertEquals(request.getTaskStatus(), capturedTask.getStatus());
            assertEquals(user.getUserId(), capturedTask.getOwner().getUserId());
            assertEquals(request.getTitle(), response.getTitle());
            assertEquals(request.getDescription(), response.getDescription());
            assertEquals(request.getTaskStatus(), response.getStatus());
            assertEquals(user.getUserId(), response.getOwner().getId());
            assertNotNull(capturedTask.getCreatedAt());
            assertNotNull(capturedTask.getUpdatedAt());
        }
        @Test
        void createTaskByCurrentUser_ShouldThrowNotFoundWhenCurrentUserDoesNotExist(){
            String username = "Anna";
            CurrentUserCreateTaskRequest request = new CurrentUserCreateTaskRequest();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.createTaskByCurrentUser(request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).save(any(Task.class));
        }
    }
    @Nested
    @DisplayName("deleteTaskByCurrentUser()")
    class DeleteTaskByCurrentUserTests{
        @Test
        void deleteTaskByCurrentUser_ShouldDeleteTaskSuccessfully(){
            long taskId = 1L;
            String username = "Anna";
            Task task = new Task();
            task.setTaskid(taskId);
            task.setOwner(user);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            SecurityContextHolder.setContext(securityContext);
            taskService.deleteTaskByCurrentUser(taskId);
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository).delete(task);
        }
        @Test
        void deleteTaskByCurrentUser_ShouldThrowNotFoundWhenTaskDoesNotExist(){
            long taskId = 1;
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.deleteTaskByCurrentUser(taskId));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Task not found", exception.getReason());
            verify(taskRepository).findById(taskId);
            verify(securityContext, never()).getAuthentication();
            verify(authentication, never()).getName();
            verify(userRepository, never()).findByUsername(anyString());
            verify(taskRepository, never()).delete(any(Task.class));
        }
        @Test
        void deleteTaskByCurrentUser_ShouldThrowNotFoundWhenCurrentUserDoesNotExist(){
            long taskId = 1L;
            String username = "Anna";
            Task task = new Task();
            task.setTaskid(taskId);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.deleteTaskByCurrentUser(taskId));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("User not found", exception.getReason());
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).delete(task);
        }
        @Test
        void deleteTaskByCurrentUser_ShouldThrowForbiddenWhenTaskBelongsToAnotherUser(){
            long taskId = 1L;
            String username = "Anna";
            User taskOwner = new User();
            taskOwner.setUserId(2L);
            taskOwner.setUsername("John");
            Task task = new Task();
            task.setTaskid(taskId);
            task.setOwner(taskOwner);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            SecurityContextHolder.setContext(securityContext);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->taskService.deleteTaskByCurrentUser(taskId));
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
            assertEquals("You can delete only your own tasks", exception.getReason());
            verify(taskRepository).findById(taskId);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByUsername(username);
            verify(taskRepository, never()).delete(any(Task.class));
        }
    }


}
