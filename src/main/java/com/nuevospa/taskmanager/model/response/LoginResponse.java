package com.nuevospa.taskmanager.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(

      @JsonProperty("token")
      String token,

      @JsonProperty("type")
      String type
) {
   public LoginResponse(String token) {
      this(token, "Bearer");
   }
}
