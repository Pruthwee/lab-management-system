package com.lab.management.controller;

import com.lab.management.entity.Patient;
import com.lab.management.service.patient.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // VIOLATION: Code Sustainability - No pagination
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // VIOLATION: Code Sustainability - String gender field
    @GetMapping("/by-gender/{gender}")
    public ResponseEntity<List<Patient>> getByGender(@PathVariable String gender) {
        return ResponseEntity.ok(patientService.getPatientsByGender(gender));
    }

    // VIOLATION: Code Sustainability - No caching for frequently accessed data
    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<Patient> getByMRN(@PathVariable String mrn) {
        return ResponseEntity.ok(patientService.getPatientByMRN(mrn));
    }

    // VIOLATION: Code Sustainability - String concatenation for request logging
    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String mrn) {

        // VIOLATION: Code Sustainability - String concatenation
        String logMessage = "";
        logMessage += "Registering patient: ";
        logMessage += firstName + " ";
        logMessage += lastName + " with MRN: ";
        logMessage += mrn;

        System.out.println(logMessage);  // Could use logging framework

        Patient patient = patientService.createPatient(firstName, lastName, mrn);
        return ResponseEntity.ok(patient);
    }
}
