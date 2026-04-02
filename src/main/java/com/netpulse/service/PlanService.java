package com.netpulse.service;

import com.netpulse.dto.PlanResponse;
import com.netpulse.model.Plan;
import com.netpulse.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    public List<PlanResponse> getActivePlans() {
        return planRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Plan getPlanById(Long planId) {
        return planRepository.findById(planId)
                .filter(Plan::isActive)
                .orElseThrow(() -> new RuntimeException("Plan not found or inactive: " + planId));
    }

    private PlanResponse mapToResponse(Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .downloadSpeedMbps(plan.getDownloadSpeedMbps())
                .uploadSpeedMbps(plan.getUploadSpeedMbps())
                .dataCapGb(plan.getDataCapGb())
                .monthlyPrice(plan.getMonthlyPrice())
                .description(plan.getDescription())
                .active(plan.isActive())
                .build();
    }
}
