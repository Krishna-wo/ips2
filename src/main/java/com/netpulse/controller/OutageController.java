// OutageController.java
package com.netpulse.controller;

import com.netpulse.dto.ApiResponse;
import com.netpulse.dto.OutageRequest;
import com.netpulse.model.OutageReport;
import com.netpulse.service.OutageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outages")
//@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OutageController {

    private final OutageService outageService;

    public OutageController(OutageService outageService) {
        this.outageService = outageService;
    }

    @PostMapping("/report")
    public ResponseEntity<ApiResponse> reportOutage(@Valid @RequestBody OutageRequest request, Authentication authentication) {
        String email = authentication.getName();
        outageService.reportOutage(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Outage reported"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveOutages() {
        List<OutageReport> outages = outageService.getActiveOutages();
        return ResponseEntity.ok(ApiResponse.ok("Active outages fetched", outages));
    }
}
