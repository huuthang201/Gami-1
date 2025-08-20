package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MonthlyDailyStatusResponse {
    String month;
    int totalDays;
    int checkedInCount;
    int maxPerMonth;
    int remaining;
    List<DailyStatus> days;
}