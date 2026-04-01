package com.netpulse.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutageRequest {
    @NotBlank(message = "Issue type required")
    private String issueType;

    @NotBlank(message = "Severity required")
    private String severity;

    private String description;
}