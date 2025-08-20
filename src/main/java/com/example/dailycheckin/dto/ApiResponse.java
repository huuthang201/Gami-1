package com.example.dailycheckin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final Instant timestamp;
    private final String path;

    public static <T> ApiResponse<T> ok(T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("OK")
                .message("Success")
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("CREATED")
                .message("Created")
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, T data, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }
}