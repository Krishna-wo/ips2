// ScheduleDtos.java
package com.netpulse.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDto {
    @NotBlank(message = "Service type required")
    private String serviceType;

    @NotNull(message = "Date required")
    private LocalDate scheduledDate;

    @NotBlank(message = "Time slot required")
    private String timeSlot;
}