// BillingController.java
package com.netpulse.controller;

import com.netpulse.dto.ApiResponse;
import com.netpulse.dto.InvoiceResponse;
import com.netpulse.dto.PaymentRequest;
import com.netpulse.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse> getInvoices(Authentication authentication) {
        String email = authentication.getName();
        List<InvoiceResponse> invoices = billingService.getInvoices(email);
        return ResponseEntity.ok(ApiResponse.ok("Invoices fetched", invoices));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPending(Authentication authentication) {
        String email = authentication.getName();
        List<InvoiceResponse> invoices = billingService.getPendingInvoices(email);
        return ResponseEntity.ok(ApiResponse.ok("Pending invoices fetched", invoices));
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse> payInvoice(@Valid @RequestBody PaymentRequest request, Authentication authentication) {
        billingService.markInvoicePaid(request.getInvoiceId(), request.getPaymentMethod());
        return ResponseEntity.ok(ApiResponse.ok("Payment processed successfully"));
    }
}