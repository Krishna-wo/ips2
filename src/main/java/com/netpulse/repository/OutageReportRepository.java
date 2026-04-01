package com.netpulse.repository;

import com.netpulse.model.Customer;
import com.netpulse.model.OutageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public  interface OutageReportRepository extends JpaRepository<OutageReport, Long> {
    List<OutageReport> findByCustomerOrderByReportedAtDesc(Customer customer);
    List<OutageReport> findByStatusNot(OutageReport.OutageStatus status);
}
