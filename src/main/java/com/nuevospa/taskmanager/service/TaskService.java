package com.nuevospa.taskmanager.service;

import com.nuevospa.taskmanager.model.generated.TaskRequest;
import com.nuevospa.taskmanager.model.generated.TaskResponse;

import java.util.List;

public interface TaskService {

   List<TaskResponse> getAllTasks();

   TaskResponse getTaskById(Long id);

   TaskResponse createTask(TaskRequest request);

   TaskResponse updateTask(Long id, TaskRequest request);

   void deleteTask(Long id);
}
