package com.example.dailycheckin.controller;

import com.example.dailycheckin.dto.ApiResponse;
import com.example.dailycheckin.dto.DeductPointRequest;
import com.example.dailycheckin.dto.TransactionPageResponse;
import com.example.dailycheckin.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/{userId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ApiResponse<TransactionPageResponse> list(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size,
                                                     HttpServletRequest request) {
        TransactionPageResponse resp = transactionService.list(userId, page, size);
        return ApiResponse.ok(resp, request.getRequestURI());
    }

    @PostMapping("/deduct")
    public ApiResponse<Map<String, Object>> deduct(@PathVariable Long userId,
                                                   @Valid @RequestBody DeductPointRequest req,
                                                   HttpServletRequest request) {
        long balance = transactionService.deduct(userId, req);
        return ApiResponse.ok(Map.of("balance", balance), request.getRequestURI());
    }
}