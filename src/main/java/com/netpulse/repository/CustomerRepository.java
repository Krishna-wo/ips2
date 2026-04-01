package com.netpulse.repository;

import com.netpulse.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public
interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserEmail(String email);
    Optional<Customer> findByAccountId(String accountId);
}
