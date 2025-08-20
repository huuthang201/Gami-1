package com.example.dailycheckin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "time_window")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeWindow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean enabled;
}