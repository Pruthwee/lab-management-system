package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// VIOLATION: Database Dependency - Using @Lob for image storage instead of external storage reference
// VIOLATION: Code Sustainability - No @Index for frequent specimen searches
@Entity
@Table(name = "specimens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specimen {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long specimenId;

    private String specimenType;  // Blood, Tissue, Urine, etc.
    private String collectionLocation;
    private LocalDateTime collectionDateTime;

    // VIOLATION: Database Dependency - LOB storage instead of external reference
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] specimenImage;

    // VIOLATION: Cloud Compatibility - Local file path instead of cloud storage
    private String localStoragePath;  // e.g., C:\lab\specimens\

    private String status;  // Active, Processed, Archived

    @OneToMany(mappedBy = "specimen", fetch = FetchType.LAZY)
    private List<LabTest> tests;
}
