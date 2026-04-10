package com.nuevospa.taskmanager.mapper;

import com.nuevospa.taskmanager.entity.Task;
import com.nuevospa.taskmanager.entity.TaskStatus;
import com.nuevospa.taskmanager.entity.User;
import com.nuevospa.taskmanager.model.request.TaskRequest;
import com.nuevospa.taskmanager.model.response.TaskResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

   public TaskResponse toResponse(Task task) {
      return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus().getName(),
            task.getCreatedAt(),
            task.getUpdatedAt()
      );
   }

   public List<TaskResponse> toResponseList(List<Task> tasks) {
      return tasks.stream()
                  .map(this::toResponse)
                  .toList();
   }

   public Task toEntity(TaskRequest request, TaskStatus status, User user) {
      return Task.builder()
                 .title(request.title())
                 .description(request.description())
                 .status(status)
                 .user(user)
                 .build();
   }
}
