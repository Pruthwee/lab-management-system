package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// VIOLATION: Code Sustainability - No @Index on frequently searched fields
@Entity
@Table(name = "test_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long methodId;

    private String methodCode;  // No @Index
    private String methodName;  // No @Index
    private String description;
    private String referenceRange;
    private String sampleRequirement;
    
    // VIOLATION: Code Sustainability - String instead of enum
    private String analysisType;  // Quantitative, Qualitative
    
    private LocalDateTime createdDate;
}
