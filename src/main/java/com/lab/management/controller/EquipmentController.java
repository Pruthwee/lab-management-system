package com.lab.management.controller;

import com.lab.management.service.equipment.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    // VIOLATION: Software Portability - Windows-specific path handling
    @GetMapping("/{equipmentId}/config-path")
    public ResponseEntity<String> getConfigPath(@PathVariable Long equipmentId) {
        String path = equipmentService.getConfigFilePath(equipmentId);
        // VIOLATION: Software Portability - Returns Windows path like C:\lab\equipment\config.xml
        return ResponseEntity.ok(path);
    }

    // VIOLATION: Cloud Compatibility - Testing hardcoded IP connectivity
    @GetMapping("/{equipmentId}/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection(@PathVariable Long equipmentId) {
        boolean connected = equipmentService.testEquipmentConnection(equipmentId);

        Map<String, Object> result = new HashMap<>();
        result.put("equipmentId", equipmentId);
        result.put("connected", connected);
        result.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(result);
    }

    // VIOLATION: Database Dependency - CLOB storage for calibration
    @PostMapping("/{equipmentId}/calibration")
    public ResponseEntity<String> uploadCalibration(
            @PathVariable Long equipmentId,
            @RequestBody String calibrationJson) {

        // VIOLATION: Database Dependency - Stores in database CLOB instead of file/cloud
        equipmentService.storeCalibrationData(equipmentId, calibrationJson);
        return ResponseEntity.ok("Calibration stored");
    }
}
