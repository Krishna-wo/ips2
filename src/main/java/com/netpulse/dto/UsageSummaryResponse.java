package com.netpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageSummaryResponse {
    private long downloadGb;
    private long uploadGb;
    private int dataCapGb;
    private double usagePercent;
    private List<DailyUsage> daily;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyUsage {
        private LocalDate date;
        private long downloadMb;
        private long uploadMb;
    }
}