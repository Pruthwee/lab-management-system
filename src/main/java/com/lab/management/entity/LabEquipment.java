package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// VIOLATION: Software Portability - Windows path hardcoded
// VIOLATION: Database Dependency - Using database-specific CLOB
@Entity
@Table(name = "lab_equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long equipmentId;

    private String equipmentName;
    private String equipmentType;  // Analyzer, Sequencer, Centrifuge
    private String ipAddress;
    private Integer portNumber;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String calibrationData;  // VIOLATION: CLOB storage

    // FIX cz-java-0057: configFilePath value should be injected via LAB_EQUIPMENT_CONFIG_PATH env var (e.g., /lab/equipment/config.xml)
    private String configFilePath;  // Injected via environment variable LAB_EQUIPMENT_CONFIG_PATH

    private LocalDateTime lastCalibrationDate;
    private String status;  // Active, Maintenance, Offline
}
