package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class CheckInResponse {
    LocalDate date;
    int awardedPoints;
    int monthlyCount;
    int monthlyMax;
    long totalPoints;
    boolean nextEligible;
}