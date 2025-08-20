package com.example.dailycheckin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "point_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private int delta;
    private long balanceAfter;
    private String description;
    private Long refId;

    @Column(name="created_at", insertable = false, updatable = false)
    private Instant createdAt;
}