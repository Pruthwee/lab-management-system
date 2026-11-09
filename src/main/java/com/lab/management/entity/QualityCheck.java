package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// VIOLATION: Database Dependency - String status instead of enum
@Entity
@Table(name = "quality_checks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long checkId;

    @ManyToOne
    private LabTechnician technician;

    private LocalDateTime checkDateTime;
    private String checkType;  // Internal, External, Proficiency
    private Double passPercentage;
    private String status;  // VIOLATION: String instead of enum (Pass, Fail, Conditional)
    private String comments;
}
