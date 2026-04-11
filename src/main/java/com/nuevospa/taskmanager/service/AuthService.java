package com.nuevospa.taskmanager.service;

import com.nuevospa.taskmanager.model.generated.LoginRequest;
import com.nuevospa.taskmanager.model.generated.LoginResponse;

public interface AuthService {

   LoginResponse login(LoginRequest request);
}
