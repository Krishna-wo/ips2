// TicketService.java
package com.netpulse.service;

import com.netpulse.dto.TicketRequest;
import com.netpulse.dto.TicketResponse;
import com.netpulse.model.Customer;
import com.netpulse.model.SupportTicket;
import com.netpulse.repository.CustomerRepository;
import com.netpulse.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final SupportTicketRepository ticketRepository;
    private final CustomerRepository customerRepository;

    public List<TicketResponse> getTickets(String email) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return ticketRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse createTicket(String email, TicketRequest request) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String ticketNumber = "TK-" + System.currentTimeMillis();

        SupportTicket ticket = new SupportTicket(ticketNumber,customer,request.getTitle(),request.getDescription(),request.getCategory(),SupportTicket.TicketStatus.OPEN);
//        SupportTicket ticket = SupportTicket.builder()
//                .ticketNumber(ticketNumber)
//                .customer(customer)
//                .title(request.getTitle())
//                .description(request.getDescription())
//                .category(request.getCategory())
//                .status(SupportTicket.TicketStatus.OPEN)
//                .build();

        ticketRepository.save(ticket);

        return mapToResponse(ticket);
    }

    private TicketResponse mapToResponse(SupportTicket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getCategory(),
                ticket.getStatus().name(),
                ticket.getCreatedAt()
        );
    }
}