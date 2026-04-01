// Additional Models
package com.netpulse.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Usage Record
@Entity
@Table(name = "usage_records")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UsageRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private LocalDate recordDate;
    private long downloadMb;
    private long uploadMb;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

