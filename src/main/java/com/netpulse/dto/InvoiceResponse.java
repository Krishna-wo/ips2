package com.netpulse.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private BigDecimal baseAmount;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private String status;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDateTime paidAt;
}

