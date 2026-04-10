package com.nuevospa.taskmanager.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record TaskResponse(

      @JsonProperty("id")
      Long id,

      @JsonProperty("title")
      String title,

      @JsonProperty("description")
      String description,

      @JsonProperty("status")
      String status,

      @JsonProperty("created_at")
      LocalDateTime createdAt,

      @JsonProperty("updated_at")
      LocalDateTime updatedAt
) {}
