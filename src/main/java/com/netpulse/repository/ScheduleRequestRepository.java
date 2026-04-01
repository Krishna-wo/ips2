package com.netpulse.repository;

import com.netpulse.model.Customer;
import com.netpulse.model.ScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface ScheduleRequestRepository extends JpaRepository<ScheduleRequest, Long> {
    List<ScheduleRequest> findByCustomerOrderByScheduledDateDesc(Customer customer);
    List<ScheduleRequest> findByScheduledDate(LocalDate date);
}
