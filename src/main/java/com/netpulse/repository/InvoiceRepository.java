package com.netpulse.repository;

import com.netpulse.model.Customer;
import com.netpulse.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCustomerOrderByIssueDateDesc(Customer customer);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT i FROM Invoice i WHERE i.customer = :customer AND i.status = 'PENDING'")
    List<Invoice> findPendingByCustomer(@Param("customer") Customer customer);
}
