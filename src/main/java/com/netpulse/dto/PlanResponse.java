package com.netpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
    private Long id;
    private String name;
    private int downloadSpeedMbps;
    private int uploadSpeedMbps;
    private int dataCapGb;
    private BigDecimal monthlyPrice;
    private String description;
    private boolean active;
}