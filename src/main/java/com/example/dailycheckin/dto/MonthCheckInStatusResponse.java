package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MonthCheckInStatusResponse {
    String month;
    List<DayStatus> days;
    int count;
    int max;
}