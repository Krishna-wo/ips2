package com.netpulse.repository;

import com.netpulse.model.Customer;
import com.netpulse.model.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public  interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    List<UsageRecord> findByCustomerAndRecordDateBetween(
            Customer customer, LocalDate from, LocalDate to);

    @Query("SELECT SUM(u.downloadMb) FROM UsageRecord u WHERE u.customer = :customer AND u.recordDate >= :from")
    Long sumDownloadSince(@Param("customer") Customer customer, @Param("from") LocalDate from);
}