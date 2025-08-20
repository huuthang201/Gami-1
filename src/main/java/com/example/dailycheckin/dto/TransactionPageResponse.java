package com.example.dailycheckin.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TransactionPageResponse {
    int page;
    int size;
    long total;
    List<TransactionItem> items;
}