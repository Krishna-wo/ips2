package com.netpulse.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticketNumber;

    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
//    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

//    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;

    public SupportTicket(String ticketNumber, Customer customer, @NotBlank(message = "Title required") String title, @NotBlank(message = "Description required") String description, @NotBlank(message = "Category required") String category, TicketStatus ticketStatus) {
        this.ticketNumber = ticketNumber;
        this.customer = customer;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = ticketStatus;

    }

    public enum TicketStatus { OPEN, IN_PROGRESS, CLOSED }
}
