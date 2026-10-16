package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// VIOLATION: Code Sustainability - Using eager loading for all relationships
@Entity
@Table(name = "lab_technicians")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabTechnician {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long technicianId;

    private String firstName;
    private String lastName;
    private String employeeId;
    private String email;

    @OneToMany(mappedBy = "technician", fetch = FetchType.EAGER)  // VIOLATION: Eager loading
    private List<QualityCheck> qualityChecks;

    private LocalDateTime hireDate;
}
