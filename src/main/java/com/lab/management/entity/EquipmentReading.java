package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// VIOLATION: Software Portability - Uses Java serialization
@Entity
@Table(name = "equipment_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentReading implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long readingId;

    @ManyToOne
    private LabEquipment equipment;

    private LocalDateTime readingDateTime;
    private Double readingValue;
    private String readingUnit;
    
    // VIOLATION: Cloud Compatibility - Hardcoded server connection
    @Transient
    private static final String SERVER_HOST = System.getenv().getOrDefault("SERVER_HOST", "10.0.0.50");  // Resolved from environment
    
    @Transient
    private static final int SERVER_PORT = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "5000"));
}
