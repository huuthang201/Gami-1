package com.example.dailycheckin.exception;

import com.example.dailycheckin.dto.ApiResponse;
import com.example.dailycheckin.dto.ErrorField;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApi(ApiException ex, HttpServletRequest req) {
        HttpStatus status = ex.getStatus();
        ApiResponse<Object> body = ApiResponse.error(
                ex.getCode(),
                ex.getMessage(),
                null,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        List<ErrorField> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorField(fe.getField(), resolveMessage(fe)))
                .toList();
        var data = Map.of("errors", errors);
        ApiResponse<Object> body = ApiResponse.error(
                "VALIDATION_ERROR",
                "Validation failed",
                data,
                req.getRequestURI()
        );
        return ResponseEntity.badRequest().body(body);
    }

    private String resolveMessage(FieldError fe) {
        if (fe.getDefaultMessage() != null) return fe.getDefaultMessage();
        return "Invalid";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleOther(Exception ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error(
                "INTERNAL_ERROR",
                "Internal server error",
                null,
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}