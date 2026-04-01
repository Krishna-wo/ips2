package com.netpulse.service;

import com.netpulse.dto.InvoiceResponse;
import com.netpulse.model.Customer;
import com.netpulse.model.Invoice;
import com.netpulse.repository.CustomerRepository;
import com.netpulse.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public List<InvoiceResponse> getInvoices(String email) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return invoiceRepository.findByCustomerOrderByIssueDateDesc(customer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponse> getPendingInvoices(String email) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return invoiceRepository.findPendingByCustomer(customer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markInvoicePaid(Long invoiceId, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getBaseAmount(),
                invoice.getGstAmount(),
                invoice.getTotalAmount(),
                invoice.getStatus().name(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getPaidAt()
        );
    }
}