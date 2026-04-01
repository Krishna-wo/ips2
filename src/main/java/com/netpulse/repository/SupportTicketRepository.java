package com.netpulse.repository;

import com.netpulse.model.Customer;
//import com.netpulse.model.SupportTicket;
import com.netpulse.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByCustomerOrderByCreatedAtDesc(Customer customer);
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
}
