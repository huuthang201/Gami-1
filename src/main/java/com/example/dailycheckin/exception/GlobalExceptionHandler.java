package com.example.dailycheckin.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApi(ApiException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of("timestamp", Instant.now(),
                             "status", ex.getStatus().value(),
                             "error", ex.getStatus().getReasonPhrase(),
                             "code", ex.getCode(),
                             "message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(Map.of("timestamp", Instant.now(),
                             "status", 500,
                             "error", "Internal Server Error",
                             "code", "INTERNAL",
                             "message", ex.getMessage()));
    }
}