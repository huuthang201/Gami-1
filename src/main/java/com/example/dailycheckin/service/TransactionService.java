package com.example.dailycheckin.service;

import com.example.dailycheckin.dto.DeductPointRequest;
import com.example.dailycheckin.dto.TransactionPageResponse;

public interface TransactionService {
    TransactionPageResponse list(Long userId, int page, int size);
    long deduct(Long userId, DeductPointRequest req);
}