package com.lab.management.controller;

import com.lab.management.entity.LabTest;
import com.lab.management.service.test.LabTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class LabTestController {

    @Autowired
    private LabTestService testService;

    @Value("${EQUIPMENT_IP:equipment-service}")
    private String equipmentIP;

    @Value("${EQUIPMENT_PORT:8888}")
    private int equipmentPort;

    // VIOLATION: Code Sustainability - Batch operation with database queries in loop
    @PostMapping("/batch-process")
    public ResponseEntity<String> processBatch(@RequestBody List<Long> testIds) {
        // VIOLATION: Code Sustainability - Database query inside loop
        testService.processMultipleTests(testIds);
        return ResponseEntity.ok("Processed " + testIds.size() + " tests");
    }

    // VIOLATION: Code Sustainability - Date range without pagination
    @GetMapping("/by-date-range")
    public ResponseEntity<List<LabTest>> getTestsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();  // No validation
        LocalDateTime end = LocalDate.parse(endDate).atStartOfDay();

        return ResponseEntity.ok(testService.getTestsInDateRange(start, end));
    }

    // VIOLATION: Cloud Compatibility - Hardcoded IP in controller
    @PostMapping("/{testId}/connect-equipment")
    public ResponseEntity<Boolean> connectEquipment(@PathVariable Long testId) {
        // Fixed: IP and port now injected via environment variables EQUIPMENT_IP and EQUIPMENT_PORT
        boolean connected = false;
        try {
            java.net.Socket socket = new java.net.Socket(equipmentIP, equipmentPort);
            connected = true;
            socket.close();
        } catch (Exception e) {
            connected = false;
        }

        return ResponseEntity.ok(connected);
    }
}
