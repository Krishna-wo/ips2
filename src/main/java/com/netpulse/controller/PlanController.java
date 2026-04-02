package com.netpulse.controller;

import com.netpulse.dto.ApiResponse;
import com.netpulse.dto.PlanResponse;
import com.netpulse.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlanController {

    private final PlanService planService;

    /**
     * GET /api/plans/active — public endpoint; lists all active plans.
     * Used by the registration form to display plan choices.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActivePlans() {
        List<PlanResponse> plans = planService.getActivePlans();
        return ResponseEntity.ok(ApiResponse.ok("Plans fetched", plans));
    }
}
