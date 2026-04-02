// UsageController.java
package com.netpulse.controller;

import com.netpulse.dto.ApiResponse;
import com.netpulse.dto.UsageSummaryResponse;
import com.netpulse.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getSummary(Authentication authentication) {
        String email = authentication.getName();
        UsageSummaryResponse summary = usageService.getMonthlyUsage(email);
        return ResponseEntity.ok(ApiResponse.ok("Usage summary fetched", summary));
    }
}