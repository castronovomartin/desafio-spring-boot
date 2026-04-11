package com.nuevospa.taskmanager.service;

import com.nuevospa.taskmanager.exception.UnauthorizedException;
import com.nuevospa.taskmanager.model.generated.LoginRequest;
import com.nuevospa.taskmanager.model.generated.LoginResponse;
import com.nuevospa.taskmanager.security.JwtUtil;
import com.nuevospa.taskmanager.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Tests")
class AuthServiceImplTest {

   @Mock
   private AuthenticationManager authenticationManager;

   @Mock
   private JwtUtil jwtUtil;

   @InjectMocks
   private AuthServiceImpl authService;

   private LoginRequest loginRequest;

   @BeforeEach
   void setUp() {
      loginRequest = new LoginRequest("admin", "password");
   }

   @Test
   @DisplayName("login - should return token when credentials are valid")
   void login_shouldReturnToken_whenCredentialsAreValid() {
      when(jwtUtil.generateToken("admin")).thenReturn("mocked.jwt.token");

      LoginResponse response = authService.login(loginRequest);

      assertThat(response).isNotNull();
      assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
      assertThat(response.getType()).isEqualTo("Bearer");
      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(jwtUtil).generateToken("admin");
   }

   @Test
   @DisplayName("login - should throw UnauthorizedException when credentials are invalid")
   void login_shouldThrowUnauthorizedException_whenCredentialsAreInvalid() {
      doThrow(new BadCredentialsException("Bad credentials"))
            .when(authenticationManager).authenticate(any());

      assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("Invalid username or password");

      verify(jwtUtil, never()).generateToken(any());
   }
}
