
package com.netpulse.service;

import com.netpulse.dto.CustomerProfileResponse;
import com.netpulse.model.Customer;
import com.netpulse.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerProfileResponse getProfile(String email) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return new CustomerProfileResponse(
                customer.getAccountId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getUser().getEmail(),
                customer.getUser().getPhone(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getPincode(),
                customer.getKycStatus().name(),
                customer.getCurrentPlan() != null ? customer.getCurrentPlan().getName() : null,
                customer.getCurrentPlan() != null ? customer.getCurrentPlan().getMonthlyPrice() : null,
                customer.getJoinDate()
        );
    }
}