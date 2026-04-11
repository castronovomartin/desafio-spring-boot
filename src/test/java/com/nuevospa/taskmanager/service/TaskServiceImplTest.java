package com.nuevospa.taskmanager.service;

import com.nuevospa.taskmanager.entity.Task;
import com.nuevospa.taskmanager.entity.TaskStatus;
import com.nuevospa.taskmanager.entity.User;
import com.nuevospa.taskmanager.exception.BadRequestException;
import com.nuevospa.taskmanager.exception.ResourceNotFoundException;
import com.nuevospa.taskmanager.mapper.TaskMapper;
import com.nuevospa.taskmanager.model.generated.TaskRequest;
import com.nuevospa.taskmanager.model.generated.TaskResponse;
import com.nuevospa.taskmanager.repository.TaskRepository;
import com.nuevospa.taskmanager.repository.TaskStatusRepository;
import com.nuevospa.taskmanager.repository.UserRepository;
import com.nuevospa.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskServiceImpl Tests")
class TaskServiceImplTest {

   @Mock
   private TaskRepository taskRepository;

   @Mock
   private TaskStatusRepository taskStatusRepository;

   @Mock
   private UserRepository userRepository;

   @Mock
   private TaskMapper taskMapper;

   @Mock
   private Authentication authentication;

   @Mock
   private SecurityContext securityContext;

   @InjectMocks
   private TaskServiceImpl taskService;

   private Task task;
   private TaskStatus taskStatus;
   private User user;
   private TaskResponse taskResponse;
   private TaskRequest taskRequest;

   @BeforeEach
   void setUp() {
      taskStatus = new TaskStatus();
      taskStatus.setId(1L);
      taskStatus.setName("PENDING");

      user = new User();
      user.setId(1L);
      user.setUsername("admin");
      user.setFullName("Administrator");

      task = Task.builder()
                 .id(1L)
                 .title("Test Task")
                 .description("Test Description")
                 .status(taskStatus)
                 .user(user)
                 .createdAt(LocalDateTime.now())
                 .updatedAt(LocalDateTime.now())
                 .build();

      taskResponse = new TaskResponse();
      taskResponse.setId(1L);
      taskResponse.setTitle("Test Task");
      taskResponse.setStatus("PENDING");

      taskRequest = new TaskRequest();
      taskRequest.setTitle("Test Task");
      taskRequest.setDescription("Test Description");
      taskRequest.setStatusId(1L);

      SecurityContextHolder.setContext(securityContext);
   }

   // -- helper privado para tests que necesitan usuario autenticado --
   private void mockAuthenticatedUser() {
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("admin");
   }

   @Test
   @DisplayName("getAllTasks - should return list of task responses")
   void getAllTasks_shouldReturnListOfTaskResponses() {
      when(taskRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(task));
      when(taskMapper.toResponseList(List.of(task))).thenReturn(List.of(taskResponse));

      List<TaskResponse> result = taskService.getAllTasks();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
      verify(taskRepository).findAllByOrderByCreatedAtDesc();
      verify(taskMapper).toResponseList(List.of(task));
   }

   @Test
   @DisplayName("getTaskById - should return task when found")
   void getTaskById_shouldReturnTask_whenFound() {
      when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
      when(taskMapper.toResponse(task)).thenReturn(taskResponse);

      TaskResponse result = taskService.getTaskById(1L);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
      verify(taskRepository).findById(1L);
   }

   @Test
   @DisplayName("getTaskById - should throw ResourceNotFoundException when not found")
   void getTaskById_shouldThrowResourceNotFoundException_whenNotFound() {
      when(taskRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> taskService.getTaskById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Task not found with id: 99");

      verify(taskMapper, never()).toResponse(any());
   }

   @Test
   @DisplayName("createTask - should create and return task response")
   void createTask_shouldCreateAndReturnTaskResponse() {
      mockAuthenticatedUser();
      when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));
      when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
      when(taskMapper.toEntity(taskRequest, taskStatus, user)).thenReturn(task);
      when(taskRepository.save(task)).thenReturn(task);
      when(taskMapper.toResponse(task)).thenReturn(taskResponse);

      TaskResponse result = taskService.createTask(taskRequest);

      assertThat(result).isNotNull();
      assertThat(result.getTitle()).isEqualTo("Test Task");
      verify(taskRepository).save(task);
   }

   @Test
   @DisplayName("createTask - should throw BadRequestException when statusId is null")
   void createTask_shouldThrowBadRequestException_whenStatusIdIsNull() {
      taskRequest.setStatusId(null);

      assertThatThrownBy(() -> taskService.createTask(taskRequest))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Status ID is required");

      verify(taskRepository, never()).save(any());
   }

   @Test
   @DisplayName("createTask - should throw BadRequestException when status not found")
   void createTask_shouldThrowBadRequestException_whenStatusNotFound() {
      taskRequest.setStatusId(99L);
      when(taskStatusRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> taskService.createTask(taskRequest))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Invalid status id: 99");

      verify(taskRepository, never()).save(any());
   }

   @Test
   @DisplayName("updateTask - should update and return task response")
   void updateTask_shouldUpdateAndReturnTaskResponse() {
      when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
      when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));
      when(taskRepository.save(task)).thenReturn(task);
      when(taskMapper.toResponse(task)).thenReturn(taskResponse);

      TaskResponse result = taskService.updateTask(1L, taskRequest);

      assertThat(result).isNotNull();
      verify(taskRepository).save(task);
   }

   @Test
   @DisplayName("deleteTask - should delete task when found")
   void deleteTask_shouldDeleteTask_whenFound() {
      when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

      taskService.deleteTask(1L);

      verify(taskRepository).delete(task);
   }

   @Test
   @DisplayName("deleteTask - should throw ResourceNotFoundException when not found")
   void deleteTask_shouldThrowResourceNotFoundException_whenNotFound() {
      when(taskRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> taskService.deleteTask(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Task not found with id: 99");

      verify(taskRepository, never()).delete(any());
   }
}
