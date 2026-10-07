package com.lab.management.service.patient;

import com.lab.management.entity.Patient;
import com.lab.management.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // VIOLATION: Code Sustainability - No pagination, loads all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();  // Could be millions in real system
    }

    // VIOLATION: Code Sustainability - String gender instead of enum
    public List<Patient> getPatientsByGender(String gender) {
        List<Patient> all = patientRepository.findAll();
        return all.stream()
                  .filter(p -> gender.equals(p.getGender()))
                  .collect(Collectors.toList());  // Manual filtering after full load
    }

    // VIOLATION: Code Sustainability - No caching for frequently accessed data
    public Patient getPatientByMRN(String mrn) {
        return patientRepository.findByMedicalRecordNumber(mrn).orElse(null);  // No cache
    }

    // VIOLATION: Code Sustainability - Eager loading of all patient relationships
    public Patient createPatient(String firstName, String lastName, String mrn) {
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setMedicalRecordNumber(mrn);
        patient.setRegistrationDate(LocalDateTime.now().toLocalDate().atStartOfDay());

        return patientRepository.save(patient);
    }
}
