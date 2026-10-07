package com.lab.management.controller;

import com.lab.management.entity.AuditLog;
import com.lab.management.service.audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    // VIOLATION: Code Sustainability - No pagination for audit logs
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllAuditLogs());
    }

    // VIOLATION: Code Sustainability - No index, full table scan
    @GetMapping("/logs/action/{action}")
    public ResponseEntity<List<AuditLog>> getLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditService.getAuditsByAction(action));
    }
}
