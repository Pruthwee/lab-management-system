package com.lab.management.entity;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;
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
    // Fixed: Hardcoded IP replaced with environment variable SERVER_HOST
    @Value("${SERVER_HOST:server-service}")
    private String serverHost;

    @Transient
    @Value("${SERVER_PORT:5000}")
    private int serverPort;
}
