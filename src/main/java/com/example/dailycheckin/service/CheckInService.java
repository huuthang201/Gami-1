package com.example.dailycheckin.service;

import com.example.dailycheckin.dto.CheckInResponse;
import com.example.dailycheckin.dto.MonthCheckInStatusResponse;
import com.example.dailycheckin.dto.MonthlyDailyStatusResponse;

public interface CheckInService {
    CheckInResponse checkIn(Long userId);
    MonthCheckInStatusResponse getMonthStatus(Long userId, String month);
    MonthlyDailyStatusResponse getMonthlyDailyStatuses(Long userId, String month);
}