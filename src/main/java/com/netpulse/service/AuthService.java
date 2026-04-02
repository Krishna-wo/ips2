package com.netpulse.service;

import com.netpulse.dto.*;
import com.netpulse.model.Customer;
import com.netpulse.model.Invoice;
import com.netpulse.model.Plan;
import com.netpulse.model.User;
import com.netpulse.repository.CustomerRepository;
import com.netpulse.repository.InvoiceRepository;
import com.netpulse.repository.PlanRepository;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
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

        // Resolve and validate plan if provided
        Plan selectedPlan = null;
        if (request.getPlanId() != null) {
            selectedPlan = planRepository.findById(request.getPlanId())
                    .filter(Plan::isActive)
                    .orElseThrow(() -> new RuntimeException("Selected plan not found or inactive"));
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
        String accountId = "NP-" + LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + user.getId();

        // Create Customer
        Customer customer = Customer.builder()
                .user(user)
                .accountId(accountId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .currentPlan(selectedPlan)
                .build();

        customer = customerRepository.save(customer);

        // Create initial invoice if a plan was selected
        if (selectedPlan != null) {
            createInitialInvoice(customer, selectedPlan);
        }

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

    private void createInitialInvoice(Customer customer, Plan plan) {
        BigDecimal base = plan.getMonthlyPrice();
        BigDecimal gst = base.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = base.add(gst);

        String invoiceNumber = "INV-" + customer.getAccountId() + "-"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .customer(customer)
                .baseAmount(base)
                .gstAmount(gst)
                .totalAmount(total)
                .status(Invoice.InvoiceStatus.PENDING)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        invoiceRepository.save(invoice);
        log.info("Initial invoice created: {} for customer {}", invoiceNumber, customer.getAccountId());
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
