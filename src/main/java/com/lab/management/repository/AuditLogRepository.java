package com.lab.management.repository;

import com.lab.management.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

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
