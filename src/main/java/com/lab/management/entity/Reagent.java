package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

// VIOLATION: Code Sustainability - No @Index on frequently searched fields
@Entity
@Table(name = "reagents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reagent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reagentId;

    private String reagentName;  // No @Index
    private String reagentCode;  // No @Index
    private String manufacturer;
    private String lotNumber;
    private LocalDate expirationDate;
    private Integer quantityOnHand;
    private String status;  // VIOLATION: String instead of enum (Active, Expired, Low Stock)
}
