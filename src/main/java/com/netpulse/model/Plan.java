package com.netpulse.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int downloadSpeedMbps;
    private int uploadSpeedMbps;
    private int dataCapGb;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    private String description;

    @Builder.Default
    private boolean active = true;
}