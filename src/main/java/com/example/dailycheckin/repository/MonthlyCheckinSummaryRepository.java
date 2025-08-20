package com.example.dailycheckin.repository;

import com.example.dailycheckin.entity.MonthlyCheckinSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyCheckinSummaryRepository extends JpaRepository<MonthlyCheckinSummary, Long> {
    Optional<MonthlyCheckinSummary> findByUserIdAndYearMonth(Long userId, String yearMonth);
}