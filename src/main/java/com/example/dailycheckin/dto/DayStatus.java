package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class DayStatus {
    int day;
    boolean checkedIn;
    Integer awardedPoints;
    LocalDateTime time;
}