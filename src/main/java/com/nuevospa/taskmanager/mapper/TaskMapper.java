package com.nuevospa.taskmanager.mapper;

import com.nuevospa.taskmanager.entity.Task;
import com.nuevospa.taskmanager.entity.TaskStatus;
import com.nuevospa.taskmanager.entity.User;
import com.nuevospa.taskmanager.model.generated.TaskRequest;
import com.nuevospa.taskmanager.model.generated.TaskResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class TaskMapper {

   public TaskResponse toResponse(Task task) {
      TaskResponse response = new TaskResponse();
      response.setId(task.getId());
      response.setTitle(task.getTitle());
      response.setDescription(task.getDescription());
      response.setStatus(task.getStatus().getName());
      response.setCreatedAt(task.getCreatedAt().atOffset(ZoneOffset.UTC));
      response.setUpdatedAt(task.getUpdatedAt().atOffset(ZoneOffset.UTC));
      return response;
   }

   public List<TaskResponse> toResponseList(List<Task> tasks) {
      return tasks.stream()
                  .map(this::toResponse)
                  .toList();
   }

   public Task toEntity(TaskRequest request, TaskStatus status, User user) {
      return Task.builder()
                 .title(request.getTitle())
                 .description(request.getDescription())
                 .status(status)
                 .user(user)
                 .build();
   }
}
