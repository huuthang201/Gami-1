package com.example.dailycheckin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "monthly_checkin_summary",
       uniqueConstraints = @UniqueConstraint(name = "uk_mcs_user_month", columnNames = {"user_id","ym"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyCheckinSummary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(name="ym", length = 6)
    private String yearMonth;

    private int checkinCount;
    private LocalDate lastCheckinDate;
    private int totalAwardedPoints;

    @Version
    @Column(name = "lock_version")
    private int version;

    @Column(name="updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
}