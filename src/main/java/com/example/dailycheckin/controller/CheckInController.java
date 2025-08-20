package com.example.dailycheckin.controller;

import com.example.dailycheckin.dto.ApiResponse;
import com.example.dailycheckin.dto.CheckInResponse;
import com.example.dailycheckin.dto.MonthCheckInStatusResponse;
import com.example.dailycheckin.dto.MonthlyDailyStatusResponse;
import com.example.dailycheckin.service.CheckInService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResponse<CheckInResponse> checkIn(@PathVariable Long userId,
                                                HttpServletRequest request) {
        CheckInResponse resp = checkInService.checkIn(userId);
        return ApiResponse.created(resp, request.getRequestURI());
    }

    @GetMapping
    public ApiResponse<MonthCheckInStatusResponse> monthStatus(@PathVariable Long userId,
                                                               @RequestParam(required = false) String month,
                                                               HttpServletRequest request) {
        MonthCheckInStatusResponse resp = checkInService.getMonthStatus(userId, month);
        return ApiResponse.ok(resp, request.getRequestURI());
    }

    @GetMapping("/status")
    public ApiResponse<MonthlyDailyStatusResponse> monthDailyStatuses(@PathVariable Long userId,
                                                                      @RequestParam(required = false) String month,
                                                                      HttpServletRequest request) {
        MonthlyDailyStatusResponse resp = checkInService.getMonthlyDailyStatuses(userId, month);
        return ApiResponse.ok(resp, request.getRequestURI());
    }
}