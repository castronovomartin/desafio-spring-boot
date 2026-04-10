package com.nuevospa.taskmanager.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(

      @NotBlank(message = "Title is required")
      @Size(max = 200, message = "Title must not exceed 200 characters")
      @JsonProperty("title")
      String title,

      @Size(max = 1000, message = "Description must not exceed 1000 characters")
      @JsonProperty("description")
      String description,

      @NotNull(message = "Status ID is required")
      @JsonProperty("status_id")
      Long statusId
) {}
