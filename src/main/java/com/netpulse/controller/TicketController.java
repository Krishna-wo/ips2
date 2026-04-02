// TicketController.java
package com.netpulse.controller;

import com.netpulse.dto.ApiResponse;
import com.netpulse.dto.TicketRequest;
//import com.netpulse.dto.TicketResponse;
import com.netpulse.dto.TicketResponse;
import com.netpulse.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<ApiResponse> getTickets(Authentication authentication) {
        String email = authentication.getName();
        List<TicketResponse> tickets = ticketService.getTickets(email);
        return ResponseEntity.ok(ApiResponse.ok("Tickets fetched", tickets));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createTicket(@Valid @RequestBody TicketRequest request, Authentication authentication) {
        String email = authentication.getName();
        TicketResponse ticket = ticketService.createTicket(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Ticket created", ticket));
    }
}