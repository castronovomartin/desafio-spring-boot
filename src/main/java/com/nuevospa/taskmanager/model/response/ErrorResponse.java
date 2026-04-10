package com.nuevospa.taskmanager.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ErrorResponse(

      @JsonProperty("message")
      String message,

      @JsonProperty("status")
      int status,

      @JsonProperty("timestamp")
      LocalDateTime timestamp
) {
   public ErrorResponse(String message, int status) {
      this(message, status, LocalDateTime.now());
   }
}
