package com.example.dailycheckin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateUserRequest {
    @NotBlank
    String username;
    String email;
}