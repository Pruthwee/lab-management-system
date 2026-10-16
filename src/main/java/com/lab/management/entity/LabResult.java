package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// VIOLATION: Code Sustainability - No pagination consideration, loads all results
// VIOLATION: Cloud Compatibility - Hardcoded database host
@Entity
@Table(name = "lab_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long resultId;

    @ManyToOne(fetch = FetchType.EAGER)  // VIOLATION: Eager loading
    private LabTest test;

    private Double resultValue;
    private String unit;
    private String referenceRange;
    private LocalDateTime analyzedDateTime;
    private String analysisStatus;  // VIOLATION: String instead of enum

    // VIOLATION: Code Sustainability - String concatenation for complex reports
    @Transient
    private String reportContent;

    private String analyzedBy;
    private LocalDateTime approvedDateTime;
}
