package com.lab.management.controller;

import com.lab.management.entity.LabResult;
import com.lab.management.service.result.LabResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
@CrossOrigin
public class LabResultController {

    @Autowired
    private LabResultService resultService;

    // VIOLATION: Code Sustainability - No pagination support
    @GetMapping
    public ResponseEntity<List<LabResult>> getAllResults() {
        List<LabResult> allResults = new ArrayList<>();  // Loads all results
        return ResponseEntity.ok(allResults);
    }

    // VIOLATION: Software Portability - Manual URL encoding
    @GetMapping("/export")
    public ResponseEntity<String> exportResults(@RequestParam String testName) {
        // VIOLATION: Software Portability - Custom URL encoding instead of URLEncoder
        String encodedName = testName.replace(" ", "%20")
                                     .replace("?", "%3F")
                                     .replace("#", "%23");
        String csvUrl = "http://localhost:8080/export/" + encodedName;
        return ResponseEntity.ok(csvUrl);
    }

    // VIOLATION: Code Sustainability - String concatenation for response building
    @GetMapping("/{resultId}/report")
    public ResponseEntity<String> getReport(@PathVariable Long resultId) {
        String report = "";
        report += "=== LAB RESULT REPORT ===\n";
        report += "Result ID: " + resultId + "\n";
        report += "Generated: " + new Date() + "\n";
        report += "Status: Complete\n";
        report += "======================\n";

        return ResponseEntity.ok(report);
    }

    // VIOLATION: Code Sustainability - No input validation, processes any string
    @PostMapping("/search")
    public ResponseEntity<List<LabResult>> searchByStatus(@RequestBody Map<String, String> request) {
        String status = request.get("status");  // VIOLATION: No validation
        // Processes status as-is
        return ResponseEntity.ok(new ArrayList<>());
    }
}
