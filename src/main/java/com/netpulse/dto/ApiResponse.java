// Generic DTOs
package com.netpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;
    private Object data;

    public static ApiResponse ok(String message) {
        return new ApiResponse(message, true, null);
    }

    public static ApiResponse ok(String message, Object data) {
        return new ApiResponse(message, true, data);
    }

    public static ApiResponse fail(String message) {
        return new ApiResponse(message, false, null);
    }
}

