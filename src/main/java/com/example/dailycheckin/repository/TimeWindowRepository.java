package com.example.dailycheckin.repository;

import com.example.dailycheckin.entity.TimeWindow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeWindowRepository extends JpaRepository<TimeWindow, Long> {
}