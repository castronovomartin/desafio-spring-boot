package com.nuevospa.taskmanager.controller;

import com.nuevospa.taskmanager.controller.api.AuthenticationApi;
import com.nuevospa.taskmanager.model.generated.LoginRequest;
import com.nuevospa.taskmanager.model.generated.LoginResponse;
import com.nuevospa.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthenticationApi {

   private final AuthService authService;

   @Override
   public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
      return ResponseEntity.ok(authService.login(loginRequest));
   }
}
