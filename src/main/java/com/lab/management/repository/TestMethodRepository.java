package com.lab.management.repository;

import com.lab.management.entity.TestMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// VIOLATION: Code Sustainability - No index on methodCode and methodName
@Repository
public interface TestMethodRepository extends JpaRepository<TestMethod, Long> {
    
    // VIOLATION: Code Sustainability - No index despite frequent searches
    Optional<TestMethod> findByMethodCode(String methodCode);
    
    // VIOLATION: Code Sustainability - Full table scan
    Optional<TestMethod> findByMethodName(String methodName);
}
