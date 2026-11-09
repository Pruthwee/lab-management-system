package com.lab.management.repository;

import com.lab.management.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

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
