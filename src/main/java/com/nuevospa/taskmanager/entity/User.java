package com.nuevospa.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, unique = true, length = 100)
   private String username;

   @Column(nullable = false)
   private String password;

   @Column(name = "full_name", nullable = false, length = 100)
   private String fullName;
}
