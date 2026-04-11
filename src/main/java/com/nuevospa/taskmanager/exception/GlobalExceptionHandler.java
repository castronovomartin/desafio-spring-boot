package com.nuevospa.taskmanager.exception;

import com.nuevospa.taskmanager.model.generated.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(ResourceNotFoundException.class)
   public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
      return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(buildError(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
   }

   @ExceptionHandler(BadRequestException.class)
   public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
   }

   @ExceptionHandler(UnauthorizedException.class)
   public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
      return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(buildError(ex.getMessage(), HttpStatus.UNAUTHORIZED.value()));
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
      String message = ex.getBindingResult()
                         .getFieldErrors()
                         .stream()
                         .map(error -> error.getField() + ": " + error.getDefaultMessage())
                         .collect(Collectors.joining(", "));

      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(message, HttpStatus.BAD_REQUEST.value()));
   }

   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
      String message = ex.getConstraintViolations()
                         .stream()
                         .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                         .collect(Collectors.joining(", "));

      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(message, HttpStatus.BAD_REQUEST.value()));
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
      return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildError("An unexpected error occurred",
                  HttpStatus.INTERNAL_SERVER_ERROR.value()));
   }

   private ErrorResponse buildError(String message, int status) {
      ErrorResponse error = new ErrorResponse();
      error.setMessage(message);
      error.setStatus(status);
      error.setTimestamp(OffsetDateTime.now());
      return error;
   }
}
