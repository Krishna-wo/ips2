// CustomerDtos.java
package com.netpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponse {
    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String kycStatus;
    private String planName;
    private BigDecimal planPrice;
    private LocalDate joinDate;
}