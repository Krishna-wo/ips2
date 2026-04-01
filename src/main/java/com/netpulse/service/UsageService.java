package com.netpulse.service;

import com.netpulse.dto.UsageSummaryResponse;
import com.netpulse.model.Customer;
import com.netpulse.model.UsageRecord;
import com.netpulse.repository.CustomerRepository;
import com.netpulse.repository.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsageService {

    private final UsageRecordRepository usageRecordRepository;
    private final CustomerRepository customerRepository;

    public UsageSummaryResponse getMonthlyUsage(String email) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);

        List<UsageRecord> records = usageRecordRepository.findByCustomerAndRecordDateBetween(
                customer, startOfMonth, now);

        long totalDownloadMb = records.stream().mapToLong(UsageRecord::getDownloadMb).sum();
        long totalUploadMb = records.stream().mapToLong(UsageRecord::getUploadMb).sum();

        int dataCap = customer.getCurrentPlan() != null ? customer.getCurrentPlan().getDataCapGb() : 500;
        long downloadGb = totalDownloadMb / 1024;
        double usagePercent = (downloadGb * 100.0) / dataCap;

        List<UsageSummaryResponse.DailyUsage> daily = records.stream()
                .map(r -> new UsageSummaryResponse.DailyUsage(r.getRecordDate(), r.getDownloadMb(), r.getUploadMb()))
                .collect(Collectors.toList());

        return new UsageSummaryResponse(downloadGb, totalUploadMb / 1024, dataCap, usagePercent, daily);
    }
}