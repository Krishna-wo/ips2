package com.netpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Outage Report
@Entity
@Table(name = "outage_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class OutageReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String issueType;

    @Enumerated(EnumType.STRING)
    private OutageSeverity severity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OutageStatus status = OutageStatus.REPORTED;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private LocalDateTime reportedAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;

    public enum OutageSeverity { COMPLETE, PARTIAL, DEGRADED }
    public enum OutageStatus { REPORTED, INVESTIGATING, RESOLVED }
}