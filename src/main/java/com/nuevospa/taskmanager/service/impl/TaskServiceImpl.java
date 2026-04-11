package com.nuevospa.taskmanager.service.impl;

import com.nuevospa.taskmanager.entity.Task;
import com.nuevospa.taskmanager.entity.TaskStatus;
import com.nuevospa.taskmanager.entity.User;
import com.nuevospa.taskmanager.exception.BadRequestException;
import com.nuevospa.taskmanager.exception.ResourceNotFoundException;
import com.nuevospa.taskmanager.mapper.TaskMapper;
import com.nuevospa.taskmanager.model.request.TaskRequest;
import com.nuevospa.taskmanager.model.response.TaskResponse;
import com.nuevospa.taskmanager.repository.TaskRepository;
import com.nuevospa.taskmanager.repository.TaskStatusRepository;
import com.nuevospa.taskmanager.repository.UserRepository;
import com.nuevospa.taskmanager.service.TaskService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

   private final TaskRepository taskRepository;
   private final TaskStatusRepository taskStatusRepository;
   private final UserRepository userRepository;
   private final TaskMapper taskMapper;

   @Override
   public List<TaskResponse> getAllTasks() {
      return taskMapper.toResponseList(taskRepository.findAllByOrderByCreatedAtDesc());
   }

   @Override
   public TaskResponse getTaskById(Long id) {
      Task task = taskRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                      "Task not found with id: " + id));
      return taskMapper.toResponse(task);
   }

   @Override
   public TaskResponse createTask(TaskRequest request) {
      TaskStatus status = taskStatusRepository.findById(request.statusId())
                                              .orElseThrow(() -> new BadRequestException(
                                                    "Invalid status id: " + request.statusId()));

      User user = getAuthenticatedUser();
      Task task = taskMapper.toEntity(request, status, user);
      return taskMapper.toResponse(taskRepository.save(task));
   }

   @Override
   public TaskResponse updateTask(Long id, TaskRequest request) {
      Task task = taskRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                      "Task not found with id: " + id));

      TaskStatus status = taskStatusRepository.findById(request.statusId())
                                              .orElseThrow(() -> new BadRequestException(
                                                    "Invalid status id: " + request.statusId()));

      task.setTitle(request.title());
      task.setDescription(request.description());
      task.setStatus(status);
      return taskMapper.toResponse(taskRepository.save(task));
   }

   @Override
   public void deleteTask(Long id) {
      Task task = taskRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                      "Task not found with id: " + id));
      taskRepository.delete(task);
   }

   private User getAuthenticatedUser() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication.getName();
      return userRepository.findByUsername(username)
                           .orElseThrow(() -> new ResourceNotFoundException(
                                 "Authenticated user not found: " + username));
   }
}
