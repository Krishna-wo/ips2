// AuthDtos.java
package com.netpulse.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
//    @NotBlank(message = "First name required")
    private String firstName;

//    @NotBlank(message = "Last name required")
    private String lastName;

//    @Email(message = "Invalid email")
//    @NotBlank
    private String email;

//    @NotBlank(message = "Phone required")
//    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

//    @NotBlank(message = "Password required")
//    @Size(min = 8, message = "Password minimum 8 characters")
    private String password;

//    @NotBlank(message = "Address required")
    private String address;

//    private String city;
//    private String state;
//    private String pincode;

    private Long planId;
}

