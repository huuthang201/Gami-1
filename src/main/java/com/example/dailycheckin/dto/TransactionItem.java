package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class TransactionItem {
    Long id;
    String type;
    int delta;
    long balanceAfter;
    String description;
    Long refId;
    Instant createdAt;
}