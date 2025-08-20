package com.example.dailycheckin.controller;

import com.example.dailycheckin.dto.CheckInResponse;
import com.example.dailycheckin.dto.MonthCheckInStatusResponse;
import com.example.dailycheckin.dto.MonthlyDailyStatusResponse;
import com.example.dailycheckin.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/checkins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInResponse checkIn(@PathVariable Long userId) {
        return checkInService.checkIn(userId);
    }

    @GetMapping
    public MonthCheckInStatusResponse monthStatus(@PathVariable Long userId,
                                                  @RequestParam(required = false) String month) {
        return checkInService.getMonthStatus(userId, month);
    }

    @GetMapping("/status")
    public MonthlyDailyStatusResponse monthDailyStatuses(@PathVariable Long userId,
                                                         @RequestParam(required = false) String month) {
        return checkInService.getMonthlyDailyStatuses(userId, month);
    }
}