
package com.netpulse.service;

import com.netpulse.dto.*;
import com.netpulse.model.Customer;
import com.netpulse.model.User;
import com.netpulse.repository.CustomerRepository;
import com.netpulse.repository.UserRepository;
import com.netpulse.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate email not exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Validate phone not exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already registered");
        }

        // Create User
        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.CUSTOMER)
                .status(User.AccountStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        // Generate Account ID
        String accountId = "NP-" + java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + user.getId();

        // Create Customer
        Customer customer = Customer.builder()
                .user(user)
                .accountId(accountId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();

        customerRepository.save(customer);

        log.info("User registered: {}", user.getEmail());

        // Generate token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_CUSTOMER")
                .build();

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getEmail(), "CUSTOMER", request.getFirstName(), accountId);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Update lastLogin
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Get customer info
        Customer customer = customerRepository.findByUserEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String token = jwtUtil.generateToken(userDetails);

        log.info("User logged in: {}", request.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getRole().name(), customer.getFirstName(), customer.getAccountId());
    }
}