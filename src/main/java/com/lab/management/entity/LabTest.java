package com.lab.management.entity;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// VIOLATION: Code Sustainability - No @Index on frequently searched test_name and status
@Entity
@Table(name = "lab_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabTest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long testId;

    private String testName;  // No @Index despite frequent searches
    private String testCode;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Specimen specimen;

    @OneToMany(mappedBy = "test", fetch = FetchType.LAZY)
    private List<LabResult> results;

    private LocalDateTime createdDate;
    private String status;  // VIOLATION: String instead of enum (Pending, Processing, Completed)

    // Fixed: Hardcoded IP replaced with environment variable EQUIPMENT_IP
    @Transient
    @Value("${EQUIPMENT_IP:equipment-service}")
    private String equipmentIP;

    @Transient
    @Value("${EQUIPMENT_PORT:8888}")
    private int equipmentPort;
}
