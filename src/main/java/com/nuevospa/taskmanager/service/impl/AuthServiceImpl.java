package com.nuevospa.taskmanager.service.impl;

import com.nuevospa.taskmanager.exception.UnauthorizedException;
import com.nuevospa.taskmanager.model.request.LoginRequest;
import com.nuevospa.taskmanager.model.response.LoginResponse;
import com.nuevospa.taskmanager.security.JwtUtil;
import com.nuevospa.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

   private final AuthenticationManager authenticationManager;
   private final JwtUtil jwtUtil;

   @Override
   public LoginResponse login(LoginRequest request) {
      try {
         authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                     request.username(),
                     request.password()
               )
         );
      } catch (BadCredentialsException ex) {
         throw new UnauthorizedException("Invalid username or password");
      }

      String token = jwtUtil.generateToken(request.username());
      return new LoginResponse(token);
   }
}
