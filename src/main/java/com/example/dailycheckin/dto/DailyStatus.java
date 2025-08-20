package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class DailyStatus {
    LocalDate date;
    boolean checkedIn;
    Integer awardedPoints;
    Long checkInId;
    LocalDateTime checkInTime;
}