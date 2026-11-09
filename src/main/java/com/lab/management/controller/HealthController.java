package com.lab.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * Controller classes have been extracted to separate files:
 * - LabResultController.java
 * - SpecimenController.java
 * - LabTestController.java
 * - PatientController.java
 * - EquipmentController.java
 * - ReagentController.java
 * - AuditController.java
 *
 * This file now contains only the HealthController for backward compatibility.
 */

@RestController
@RequestMapping("/")
public class HealthController {

    // VIOLATION: Cloud Compatibility - Hardcoded service URLs
    @GetMapping
    public ResponseEntity<Map<String, String>> getApiInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("service", "Laboratory Management System");
        info.put("version", "1.0.0");

        // VIOLATION: Cloud Compatibility - Hardcoded URLs
        info.put("results_api", "http://localhost:8080/api/results");
        info.put("specimens_api", "http://localhost:8080/api/specimens");
        info.put("tests_api", "http://localhost:8080/api/tests");
        info.put("patients_api", "http://localhost:8080/api/patients");
        info.put("equipment_api", "http://localhost:8080/api/equipment");
        info.put("reagents_api", "http://localhost:8080/api/reagents");
        info.put("audit_api", "http://localhost:8080/api/audit");

        return ResponseEntity.ok(info);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", new Date().toString());
        return ResponseEntity.ok(status);
    }
}
