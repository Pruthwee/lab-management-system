package com.lab.management.controller;

import com.lab.management.entity.Specimen;
import com.lab.management.service.specimen.SpecimenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/specimens")
public class SpecimenController {

    @Autowired
    private SpecimenService specimenService;

    // VIOLATION: Code Sustainability - No pagination for specimen list
    @GetMapping
    public ResponseEntity<List<Specimen>> getAllSpecimens() {
        return ResponseEntity.ok(specimenService.getAllSpecimens());
    }

    // VIOLATION: Cloud Compatibility - Local file system path return
    @PostMapping("/{specimenId}/store")
    public ResponseEntity<String> storeSpecimen(
            @PathVariable Long specimenId,
            @RequestBody byte[] imageData) {

        Specimen specimen = new Specimen();
        specimen.setSpecimenId(specimenId);

        // VIOLATION: Cloud Compatibility - Stores locally instead of cloud
        specimenService.storeSpecimenLocally(specimen, imageData);

        return ResponseEntity.ok("Stored locally: " + specimen.getLocalStoragePath());
    }

    // VIOLATION: Code Sustainability - Hardcoded response format
    @GetMapping("/{specimenId}/info")
    public ResponseEntity<Map<String, Object>> getSpecimenInfo(@PathVariable Long specimenId) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", specimenId);
        info.put("timestamp", System.currentTimeMillis());
        info.put("format", "JSON");  // Hardcoded format

        return ResponseEntity.ok(info);
    }
}
