package com.example.dailycheckin.controller;

import com.example.dailycheckin.dto.DeductPointRequest;
import com.example.dailycheckin.dto.TransactionPageResponse;
import com.example.dailycheckin.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public TransactionPageResponse list(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        return transactionService.list(userId, page, size);
    }

    @PostMapping("/deduct")
    public Object deduct(@PathVariable Long userId,
                         @Valid @RequestBody DeductPointRequest req) {
        long balance = transactionService.deduct(userId, req);
        return java.util.Map.of("balance", balance);
    }
}