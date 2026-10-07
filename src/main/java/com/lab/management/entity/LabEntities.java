package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
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
    private static final String EQUIPMENT_IP = "192.168.1.100";  // Hardcoded IP
    
    @Transient
    private static final int EQUIPMENT_PORT = 8888;
}

// VIOLATION: Code Sustainability - No pagination for patient records
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long patientId;

    private String firstName;
    private String lastName;
    private String medicalRecordNumber;  // No @Index despite being frequently searched
    private LocalDate dateOfBirth;
    
    @OneToMany(mappedBy = "patient")
    private List<Specimen> specimens;

    private LocalDateTime registrationDate;
    private String gender;  // VIOLATION: String instead of enum
}

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
    
    // VIOLATION: Software Portability - Windows-specific path
    private String configFilePath;  // e.g., "C:\\lab\\equipment\\config.xml"
    
    private LocalDateTime lastCalibrationDate;
    private String status;  // Active, Maintenance, Offline
}

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

// VIOLATION: Code Sustainability - Using eager loading for all relationships
@Entity
@Table(name = "lab_technicians")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabTechnician {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long technicianId;

    private String firstName;
    private String lastName;
    private String employeeId;
    private String email;
    
    @OneToMany(mappedBy = "technician", fetch = FetchType.EAGER)  // VIOLATION: Eager loading
    private List<QualityCheck> qualityChecks;

    private LocalDateTime hireDate;
}

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

// VIOLATION: Code Sustainability - No indexes on frequently searched audit fields
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long logId;

    private String action;  // No @Index despite frequent searches
    private String entityName;
    private String entityId;
    private LocalDateTime timestamp;
    private String performedBy;
    private String details;
}

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
    private static final String SERVER_HOST = "10.0.0.50";
    
    @Transient
    private static final int SERVER_PORT = 5000;
}
