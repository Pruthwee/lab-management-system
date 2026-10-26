package com.lab.management.repository;

import com.lab.management.entity.LabTechnician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// VIOLATION: Code Sustainability - Eager loading means N+1 for quality checks
@Repository
public interface LabTechnicianRepository extends JpaRepository<LabTechnician, Long> {
    
    // VIOLATION: Code Sustainability - Eager loading causes N+1 if many exist
    List<LabTechnician> findAll();
    
    Optional<LabTechnician> findByEmployeeId(String employeeId);
}
