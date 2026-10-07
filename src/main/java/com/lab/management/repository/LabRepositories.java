package com.lab.management.repository;

import com.lab.management.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// VIOLATION: Code Sustainability - No pagination, findAll() without Pageable
// VIOLATION: Code Sustainability - N+1 query issue with eager loading
@Repository
public interface SpecimenRepository extends JpaRepository<Specimen, Long> {
    
    // VIOLATION: Code Sustainability - No index on status field, full table scan
    List<Specimen> findByStatus(String status);
    
    // VIOLATION: Code Sustainability - No pagination support for large result sets
    List<Specimen> findAll();
    
    // VIOLATION: Code Sustainability - Lazy loading means N+1 if not careful
    List<Specimen> findByCollectionLocationIgnoreCase(String location);
}

// VIOLATION: Code Sustainability - N+1 query issue with eager loaded test
@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    
    // VIOLATION: Code Sustainability - Fetches all results, no pagination
    List<LabResult> findByTestTestName(String testName);
    
    // VIOLATION: Code Sustainability - String analysis status, causes full scan
    List<LabResult> findByAnalysisStatus(String status);
}

// VIOLATION: Code Sustainability - No index on testName and status fields
@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    
    // VIOLATION: Code Sustainability - N+1 problem with lazy-loaded specimen
    List<LabTest> findByTestName(String testName);
    
    // VIOLATION: Code Sustainability - String status field, full table scan
    List<LabTest> findByStatus(String status);
    
    // VIOLATION: Code Sustainability - No pagination, loads all by date
    List<LabTest> findByCreatedDateBetween(LocalDate start, LocalDate end);
}

// VIOLATION: Code Sustainability - No index on MRN, full table scan
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    // VIOLATION: Code Sustainability - No index despite frequent searches
    Optional<Patient> findByMedicalRecordNumber(String mrn);
    
    // VIOLATION: Code Sustainability - Full table scan for first name search
    List<Patient> findByFirstNameIgnoreCase(String firstName);
    
    // VIOLATION: Code Sustainability - No pagination for patient list
    List<Patient> findAll();
}

// VIOLATION: Cloud Compatibility - Using custom SQL with hardcoded database host
@Repository
public interface LabEquipmentRepository extends JpaRepository<LabEquipment, Long> {
    
    // VIOLATION: Database Dependency - SQL Server DATEPART if this were real
    List<LabEquipment> findByStatus(String status);
    
    // VIOLATION: Cloud Compatibility - IP address lookup (hardcoded in entity)
    Optional<LabEquipment> findByIpAddress(String ipAddress);
}

// VIOLATION: Code Sustainability - No index on methodCode and methodName
@Repository
public interface TestMethodRepository extends JpaRepository<TestMethod, Long> {
    
    // VIOLATION: Code Sustainability - No index despite frequent searches
    Optional<TestMethod> findByMethodCode(String methodCode);
    
    // VIOLATION: Code Sustainability - Full table scan
    Optional<TestMethod> findByMethodName(String methodName);
}

// VIOLATION: Code Sustainability - Eager loading means N+1 for quality checks
@Repository
public interface LabTechnicianRepository extends JpaRepository<LabTechnician, Long> {
    
    // VIOLATION: Code Sustainability - Eager loading causes N+1 if many exist
    List<LabTechnician> findAll();
    
    Optional<LabTechnician> findByEmployeeId(String employeeId);
}

// VIOLATION: Code Sustainability - No index on checkType and status
@Repository
public interface QualityCheckRepository extends JpaRepository<QualityCheck, Long> {
    
    // VIOLATION: Code Sustainability - String status field, full table scan
    List<QualityCheck> findByStatus(String status);
    
    // VIOLATION: Code Sustainability - String type field, full table scan
    List<QualityCheck> findByCheckType(String checkType);
}

// VIOLATION: Code Sustainability - No index on reagentName, reagentCode
@Repository
public interface ReagentRepository extends JpaRepository<Reagent, Long> {
    
    // VIOLATION: Code Sustainability - No index despite frequent searches
    Optional<Reagent> findByReagentCode(String reagentCode);
    
    // VIOLATION: Code Sustainability - String status, full table scan
    List<Reagent> findByStatus(String status);
    
    // VIOLATION: Code Sustainability - No pagination for inventory list
    List<Reagent> findAll();
}

// VIOLATION: Code Sustainability - No index on action, entityName
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    // VIOLATION: Code Sustainability - No index despite frequent audit queries
    List<AuditLog> findByAction(String action);
    
    // VIOLATION: Code Sustainability - No pagination, loads all audit logs
    List<AuditLog> findAll();
    
    // VIOLATION: Code Sustainability - String entityName, full table scan
    List<AuditLog> findByEntityName(String entityName);
}

// VIOLATION: Cloud Compatibility - Hardcoded server connection in entity
@Repository
public interface EquipmentReadingRepository extends JpaRepository<EquipmentReading, Long> {
    
    List<EquipmentReading> findByEquipmentEquipmentId(Long equipmentId);
}
