package com.lab.management.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {
    private String firstName;
    private String lastName;
    private String medicalRecordNumber;
    private LocalDate dateOfBirth;
    private String gender;
    // VIOLATION: Code Sustainability - No validation annotations
}
