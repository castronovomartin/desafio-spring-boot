package com.nuevospa.taskmanager.controller;

import com.nuevospa.taskmanager.controller.api.TasksApi;
import com.nuevospa.taskmanager.model.generated.TaskRequest;
import com.nuevospa.taskmanager.model.generated.TaskResponse;
import com.nuevospa.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController implements TasksApi {

   private final TaskService taskService;

   @Override
   public ResponseEntity<List<TaskResponse>> getTasks() {
      return ResponseEntity.ok(taskService.getAllTasks());
   }

   @Override
   public ResponseEntity<TaskResponse> getTaskById(Long id) {
      return ResponseEntity.ok(taskService.getTaskById(id));
   }

   @Override
   public ResponseEntity<TaskResponse> createTask(TaskRequest taskRequest) {
      return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(taskService.createTask(taskRequest));
   }

   @Override
   public ResponseEntity<TaskResponse> updateTask(Long id, TaskRequest taskRequest) {
      return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
   }

   @Override
   public ResponseEntity<Void> deleteTask(Long id) {
      taskService.deleteTask(id);
      return ResponseEntity.noContent().build();
   }
}
