package com.example.barogo.advice;

import com.example.barogo.dto.ApiResponse;
import com.example.barogo.exception.PasswordExpiredException;
import com.example.barogo.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        Map<String, String> body = Map.of("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(PasswordExpiredException.class)
    public ResponseEntity<Map<String, String>> handlePasswordExpired(PasswordExpiredException ex) {
        Map<String, String> body = Map.of("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
