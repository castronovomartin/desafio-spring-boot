package com.nuevospa.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 200)
   private String title;

   @Column(length = 1000)
   private String description;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "status_id", nullable = false)
   private TaskStatus status;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @Column(name = "updated_at", nullable = false)
   private LocalDateTime updatedAt;

   @PrePersist
   protected void onCreate() {
      createdAt = LocalDateTime.now();
      updatedAt = LocalDateTime.now();
   }

   @PreUpdate
   protected void onUpdate() {
      updatedAt = LocalDateTime.now();
   }
}
