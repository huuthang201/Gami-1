package com.example.dailycheckin.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeductPointRequest {
    @Min(1)
    int points;
    String reason;
}