package com.lab.management.repository;

import com.lab.management.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// VIOLATION: Code Sustainability - N+1 query issue with eager loaded test
@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {

    // VIOLATION: Code Sustainability - Fetches all results, no pagination
    List<LabResult> findByTestTestName(String testName);

    // VIOLATION: Code Sustainability - String analysis status, causes full scan
    List<LabResult> findByAnalysisStatus(String status);
}
