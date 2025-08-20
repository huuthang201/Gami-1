package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserProfileResponse {
    Long id;
    String username;
    String email;
    long totalPoints;
    int currentMonthCheckinCount;
    int currentMonthMax;
    int remaining;
}