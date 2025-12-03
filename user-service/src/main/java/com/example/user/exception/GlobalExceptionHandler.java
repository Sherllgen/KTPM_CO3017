package com.example.user.exception;

import com.example.user.config.ApiResponse;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<Object> response = new ApiResponse<>(
                errorCode.getCode(),
                ex.getMessage(),
                null);
        return ResponseEntity.status(errorCode.getStatus().value()).body(response);
    }

    // Handle Validation Error (Bean Validation @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiResponse<Object> response = new ApiResponse<>(
                ErrorCode.INVALID_REQUEST.getCode(),
                errorMessage,
                null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle unexpected error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiResponse<Object> response = new ApiResponse<>(
                errorCode.getCode(),
                errorCode.getMessage(),
                null);
        return ResponseEntity.status(errorCode.getStatus().value()).body(response);
    }
}
