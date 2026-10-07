package com.lab.management.service.audit;

import com.lab.management.entity.AuditLog;
import com.lab.management.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditService {

    @Autowired
    private AuditLogRepository auditRepository;

    // VIOLATION: Code Sustainability - String action/entity names instead of enum
    public void logAction(String action, String entityName, String entityId, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);  // String instead of enum
        log.setEntityName(entityName);  // String instead of enum
        log.setEntityId(entityId);
        log.setTimestamp(LocalDateTime.now());
        log.setPerformedBy("SYSTEM");
        log.setDetails(details);

        auditRepository.save(log);
    }

    // VIOLATION: Code Sustainability - No pagination for audit logs
    public List<AuditLog> getAllAuditLogs() {
        return auditRepository.findAll();  // Could be millions of logs
    }

    // VIOLATION: Code Sustainability - No index on action field
    public List<AuditLog> getAuditsByAction(String action) {
        return auditRepository.findByAction(action);  // Full table scan
    }
}
