package com.netpulse.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(unique = true)
    private String accountId;

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private String kycDocType;
    private String kycDocNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private KycStatus kycStatus = KycStatus.PENDING;

    @Builder.Default
    private LocalDate joinDate = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan currentPlan;

    public enum KycStatus { PENDING, VERIFIED, REJECTED }
}