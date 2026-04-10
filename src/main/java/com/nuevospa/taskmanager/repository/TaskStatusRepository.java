package com.nuevospa.taskmanager.repository;

import com.nuevospa.taskmanager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
}
