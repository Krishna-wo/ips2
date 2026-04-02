// CustomerController.java
package com.netpulse.controller;

import com.netpulse.dto.ApiResponse;
import com.netpulse.dto.CustomerProfileResponse;
import com.netpulse.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        CustomerProfileResponse profile = customerService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.ok("Profile fetched", profile));
    }
}