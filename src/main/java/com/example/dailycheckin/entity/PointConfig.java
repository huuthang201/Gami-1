package com.example.dailycheckin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointConfig {
    @Id
    @Column(name = "order_index")
    private Integer orderIndex;

    private Integer points;

    private java.time.LocalDate activeFrom;
    private java.time.LocalDate activeTo;

    private Boolean enabled;
}