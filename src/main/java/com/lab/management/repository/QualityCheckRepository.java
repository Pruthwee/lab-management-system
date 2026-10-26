package com.lab.management.repository;

import com.lab.management.entity.QualityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// VIOLATION: Code Sustainability - No index on checkType and status
@Repository
public interface QualityCheckRepository extends JpaRepository<QualityCheck, Long> {
    
    // VIOLATION: Code Sustainability - String status field, full table scan
    List<QualityCheck> findByStatus(String status);
    
    // VIOLATION: Code Sustainability - String type field, full table scan
    List<QualityCheck> findByCheckType(String checkType);
}
