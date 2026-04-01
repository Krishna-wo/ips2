package com.netpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Schedule Request
@Entity
@Table(name = "schedule_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 public  class ScheduleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String serviceType;
    private LocalDate scheduledDate;
    private String timeSlot;
    private String technicianName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ScheduleStatus { SCHEDULED, COMPLETED, CANCELLED }
}