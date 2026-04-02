// OutageService.java
package com.netpulse.service;

import com.netpulse.dto.OutageRequest;
import com.netpulse.model.Customer;
import com.netpulse.model.OutageReport;
import com.netpulse.repository.CustomerRepository;
import com.netpulse.repository.OutageReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class OutageService {

    private final OutageReportRepository outageRepository;
    private final CustomerRepository customerRepository;

    public OutageService(OutageReportRepository outageRepository, CustomerRepository customerRepository) {
        this.outageRepository = outageRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void reportOutage(String email, OutageRequest request) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        OutageReport report = OutageReport.builder()
                .customer(customer)
                .issueType(request.getIssueType())
                .severity(OutageReport.OutageSeverity.valueOf(request.getSeverity()))
                .description(request.getDescription())
                .status(OutageReport.OutageStatus.REPORTED)
                .build();

        outageRepository.save(report);
    }

    public List<OutageReport> getActiveOutages() {
        return outageRepository.findByStatusNot(OutageReport.OutageStatus.RESOLVED);
    }
}