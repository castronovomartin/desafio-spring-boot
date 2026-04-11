package com.nuevospa.taskmanager.service.impl;

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
import com.nuevospa.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

   private final TaskRepository taskRepository;
   private final TaskStatusRepository taskStatusRepository;
   private final UserRepository userRepository;
   private final TaskMapper taskMapper;

   private record TaskValidationData(Task task, TaskStatus status) {}

   @Override
   @Transactional(readOnly = true)
   public List<TaskResponse> getAllTasks() {
      return taskMapper.toResponseList(
            taskRepository.findAllByOrderByCreatedAtDesc());
   }

   @Override
   @Transactional(readOnly = true)
   public TaskResponse getTaskById(Long id) {
      Task task = findTaskById(id);
      return taskMapper.toResponse(task);
   }

   @Override
   public TaskResponse createTask(TaskRequest request) {
      TaskStatus status = findTaskStatus(request.getStatusId());
      User user = getAuthenticatedUser();
      Task task = taskMapper.toEntity(request, status, user);
      return taskMapper.toResponse(taskRepository.save(task));
   }

   @Override
   public TaskResponse updateTask(Long id, TaskRequest request) {
      TaskValidationData validationData = new TaskValidationData(
            findTaskById(id),
            findTaskStatus(request.getStatusId())
      );

      validationData.task().setTitle(request.getTitle());
      validationData.task().setDescription(request.getDescription());
      validationData.task().setStatus(validationData.status());

      return taskMapper.toResponse(
            taskRepository.save(validationData.task()));
   }

   @Override
   public void deleteTask(Long id) {
      taskRepository.delete(findTaskById(id));
   }

   private Task findTaskById(Long id) {
      return taskRepository.findById(id)
                           .orElseThrow(() -> new ResourceNotFoundException(
                                 "Task not found with id: " + id));
   }

   private TaskStatus findTaskStatus(Long statusId) {
      if (statusId == null) {
         throw new BadRequestException("Status ID is required");
      }
      return taskStatusRepository.findById(statusId)
                                 .orElseThrow(() -> new BadRequestException(
                                       "Invalid status id: " + statusId));
   }

   private User getAuthenticatedUser() {
      Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
      return userRepository.findByUsername(authentication.getName())
                           .orElseThrow(() -> new ResourceNotFoundException(
                                 "Authenticated user not found: " + authentication.getName()));
   }
}
