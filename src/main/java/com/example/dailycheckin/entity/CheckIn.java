package com.example.dailycheckin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "check_in",
       uniqueConstraints = @UniqueConstraint(name = "uk_ci_user_date", columnNames = {"user_id","check_in_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckIn {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name="awarded_points", nullable = false)
    private Integer awardedPoints;

    @Column(name="created_at", insertable = false, updatable = false)
    private Instant createdAt;
}