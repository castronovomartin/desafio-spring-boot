package com.nuevospa.taskmanager.service;

import com.nuevospa.taskmanager.model.request.LoginRequest;
import com.nuevospa.taskmanager.model.response.LoginResponse;

public interface AuthService {

   LoginResponse login(LoginRequest request);
}
