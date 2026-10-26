package com.lab.management.entity;

import jakarta.persistence.*;
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
    
    // VIOLATION: Cloud Compatibility - Hardcoded equipment IP
    @Transient
    private static final String EQUIPMENT_IP = System.getenv().getOrDefault("EQUIPMENT_IP", "192.168.1.100");  // Resolved from environment
    
    @Transient
    private static final int EQUIPMENT_PORT = Integer.parseInt(System.getenv().getOrDefault("EQUIPMENT_PORT", "8888"));
}
