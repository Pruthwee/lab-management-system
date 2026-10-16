package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// VIOLATION: Code Sustainability - No pagination for patient records
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long patientId;

    private String firstName;
    private String lastName;
    private String medicalRecordNumber;  // No @Index despite being frequently searched
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "patient")
    private List<Specimen> specimens;

    private LocalDateTime registrationDate;
    private String gender;  // VIOLATION: String instead of enum
}
