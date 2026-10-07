package com.lab.management.controller;

import com.lab.management.entity.LabTest;
import com.lab.management.service.test.LabTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class LabTestController {

    @Autowired
    private LabTestService testService;

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

        LocalDate start = LocalDate.parse(startDate);  // No validation
        LocalDate end = LocalDate.parse(endDate);

        return ResponseEntity.ok(testService.getTestsInDateRange(start, end));
    }

    // VIOLATION: Cloud Compatibility - Hardcoded IP in controller
    @PostMapping("/{testId}/connect-equipment")
    public ResponseEntity<Boolean> connectEquipment(@PathVariable Long testId) {
        // VIOLATION: Cloud Compatibility - Hardcoded IP address
        String equipmentIP = "192.168.1.100";
        int port = 8888;

        boolean connected = false;
        try {
            java.net.Socket socket = new java.net.Socket(equipmentIP, port);
            connected = true;
            socket.close();
        } catch (Exception e) {
            connected = false;
        }

        return ResponseEntity.ok(connected);
    }
}
