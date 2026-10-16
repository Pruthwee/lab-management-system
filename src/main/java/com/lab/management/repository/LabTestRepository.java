package com.lab.management.repository;

import com.lab.management.entity.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

// VIOLATION: Code Sustainability - No index on testName and status fields
@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    // VIOLATION: Code Sustainability - N+1 problem with lazy-loaded specimen
    List<LabTest> findByTestName(String testName);

    // VIOLATION: Code Sustainability - String status field, full table scan
    List<LabTest> findByStatus(String status);

    // VIOLATION: Code Sustainability - No pagination, loads all by date
    List<LabTest> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end);
}
