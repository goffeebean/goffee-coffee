package com.goffeebean.exception;

import com.goffeebean.dto.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleNotFound(RoastNotFoundException ex) {
        ApiError apiError = new ApiError(Instant.now(), 404, "Not Found",
                ex.getMessage(), List.of());
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        ApiError apiError = new ApiError(Instant.now(), 400, "Bad Request",
                "Validation failed", ex.getBindingResult().getFieldErrors().stream().map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).toList());
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleOllamaUnavailable(OllamaUnavailableException ex) {
        ApiError apiError = new ApiError(Instant.now(), 503, "Ollama Unavailable",
                ex.getMessage(), List.of());
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
}
