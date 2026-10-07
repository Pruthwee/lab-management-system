package com.lab.management.service;

import com.lab.management.entity.*;
import com.lab.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LabResultService {
    
    @Autowired
    private LabResultRepository resultRepository;
    
    @Autowired
    private LabTestRepository testRepository;
    
    // VIOLATION: Code Sustainability - String concatenation instead of StringBuilder
    public String generateComprehensiveReport(Long resultId) {
        LabResult result = resultRepository.findById(resultId).orElse(null);
        if (result == null) return "";
        
        // VIOLATION: Code Sustainability - Inefficient string concatenation
        String report = "";
        report += "Test Name: " + result.getTest().getTestName() + "\n";
        report += "Result Value: " + result.getResultValue() + "\n";
        report += "Unit: " + result.getUnit() + "\n";
        report += "Reference Range: " + result.getReferenceRange() + "\n";
        report += "Analysis Status: " + result.getAnalysisStatus() + "\n";
        report += "Analyzed Date: " + result.getAnalyzedDateTime() + "\n";
        report += "Analyzed By: " + result.getAnalyzedBy() + "\n";
        report += "Approved Date: " + result.getApprovedDateTime() + "\n";
        
        // Could continue with more concatenations
        return report;
    }
    
    // VIOLATION: Code Sustainability - N+1 problem: loads all results without index
    public List<LabResult> getResultsByTestName(String testName) {
        return resultRepository.findByTestTestName(testName);  // No index on testName
    }
    
    // VIOLATION: Code Sustainability - No caching, queries database every time
    public LabResult getResultWithoutCaching(Long resultId) {
        return resultRepository.findById(resultId).orElse(null);
    }
    
    // VIOLATION: Code Sustainability - Full table scan due to string status
    public List<LabResult> getPendingResults() {
        return resultRepository.findByAnalysisStatus("Pending");  // String instead of enum
    }
    
    // VIOLATION: Code Sustainability - Manual string building for URL
    public String buildResultUrl(Long resultId, String baseUrl) {
        String url = baseUrl + "?resultId=" + resultId + "&format=json&timestamp=" + 
                     System.currentTimeMillis() + "&user=lab_tech";
        return url;  // Should use URLEncoder and proper URL building
    }
}

@Service
@Transactional
public class SpecimenService {
    
    @Autowired
    private SpecimenRepository specimenRepository;
    
    @Autowired
    private LabTestRepository testRepository;
    
    // VIOLATION: Code Sustainability - No pagination, loads all specimens
    public List<Specimen> getAllSpecimens() {
        return specimenRepository.findAll();  // Could be millions of records
    }
    
    // VIOLATION: Cloud Compatibility - Using local file system path
    public void storeSpecimenLocally(Specimen specimen, byte[] imageData) {
        // VIOLATION: Software Portability - Windows-specific path
        String filePath = "C:\\lab\\specimens\\" + specimen.getSpecimenId() + ".img";
        specimen.setLocalStoragePath(filePath);
        specimenRepository.save(specimen);
    }
    
    // VIOLATION: Code Sustainability - N+1 problem loading all tests per specimen
    public Map<Long, Integer> getSpecimenTestCounts() {
        List<Specimen> specimens = specimenRepository.findAll();
        Map<Long, Integer> counts = new HashMap<>();
        
        for (Specimen specimen : specimens) {
            // VIOLATION: Database Dependency - Accessing lazy-loaded collection
            int testCount = specimen.getTests().size();  // N+1 query here!
            counts.put(specimen.getSpecimenId(), testCount);
        }
        
        return counts;
    }
    
    // VIOLATION: Code Sustainability - String status instead of enum
    public List<Specimen> getActiveSpecimens() {
        return specimenRepository.findByStatus("Active");
    }
    
    // VIOLATION: Code Sustainability - Naive string search algorithm O(n²)
    public List<Specimen> searchSpecimensByType(String specimenType) {
        List<Specimen> all = specimenRepository.findAll();
        List<Specimen> results = new ArrayList<>();
        
        for (Specimen s : all) {
            if (s.getSpecimenType().substring(0).contains(specimenType)) {
                results.add(s);  // Naive contains, no index usage
            }
        }
        
        return results;
    }
}

@Service
@Transactional
public class LabTestService {
    
    @Autowired
    private LabTestRepository testRepository;
    
    @Autowired
    private LabEquipmentRepository equipmentRepository;
    
    // VIOLATION: Code Sustainability - Database query in loop
    public void processMultipleTests(List<Long> testIds) {
        for (Long testId : testIds) {
            // VIOLATION: Code Sustainability - Database query inside loop!
            LabTest test = testRepository.findById(testId).orElse(null);
            if (test != null) {
                test.setStatus("Processing");
                testRepository.save(test);  // Another query
            }
        }
    }
    
    // VIOLATION: Code Sustainability - No pagination for date range
    public List<LabTest> getTestsInDateRange(LocalDate start, LocalDate end) {
        return testRepository.findByCreatedDateBetween(start, end);  // Could be millions
    }
    
    // VIOLATION: Cloud Compatibility - Hardcoded IP address access
    public void connectToEquipment() {
        String equipmentIP = "192.168.1.100";  // Hardcoded from entity
        int port = 8888;
        // Would fail if IP changes
    }
    
    // VIOLATION: Code Sustainability - No index on testName field
    public Optional<LabTest> findByTestName(String testName) {
        List<LabTest> tests = testRepository.findByTestName(testName);  // Full table scan
        return tests.stream().findFirst();
    }
    
    // VIOLATION: Code Sustainability - String status field
    public List<LabTest> getCompletedTests() {
        return testRepository.findByStatus("Completed");  // String instead of enum
    }
}

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

@Service
@Transactional
public class EquipmentService {
    
    @Autowired
    private LabEquipmentRepository equipmentRepository;
    
    // VIOLATION: Software Portability - Windows-specific path handling
    public String getConfigFilePath(Long equipmentId) {
        LabEquipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        if (equipment != null) {
            return equipment.getConfigFilePath();  // e.g., "C:\\lab\\equipment\\config.xml"
        }
        return null;
    }
    
    // VIOLATION: Code Sustainability - String status instead of enum
    public List<LabEquipment> getActiveEquipment() {
        return equipmentRepository.findByStatus("Active");
    }
    
    // VIOLATION: Cloud Compatibility - Hardcoded IP connectivity
    public boolean testEquipmentConnection(Long equipmentId) {
        LabEquipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        if (equipment == null) return false;
        
        // VIOLATION: Cloud Compatibility - Hardcoded IP:Port
        try {
            Socket socket = new Socket(equipment.getIpAddress(), equipment.getPortNumber());
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // VIOLATION: Code Sustainability - BLOB storage instead of external reference
    public void storeCalibrationData(Long equipmentId, String calibrationJson) {
        LabEquipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        if (equipment != null) {
            // VIOLATION: Database Dependency - CLOB storage in database
            equipment.setCalibrationData(calibrationJson);
            equipmentRepository.save(equipment);
        }
    }
}

@Service
@Transactional
public class ReagentService {
    
    @Autowired
    private ReagentRepository reagentRepository;
    
    // VIOLATION: Code Sustainability - No pagination
    public List<Reagent> getAllReagents() {
        return reagentRepository.findAll();
    }
    
    // VIOLATION: Code Sustainability - String status instead of enum
    public List<Reagent> getLowStockReagents() {
        List<Reagent> all = reagentRepository.findByStatus("Low Stock");
        return all;  // Could filter application-side
    }
    
    // VIOLATION: Code Sustainability - No caching for inventory checks
    public Integer getReagentQuantity(String reagentCode) {
        Optional<Reagent> reagent = reagentRepository.findByReagentCode(reagentCode);
        return reagent.map(Reagent::getQuantityOnHand).orElse(0);  // No cache
    }
}

@Service
@Transactional
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditRepository;
    
    // VIOLATION: Code Sustainability - String action/entity names instead of enum
    public void logAction(String action, String entityName, String entityId, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);  // String instead of enum
        log.setEntityName(entityName);  // String instead of enum
        log.setEntityId(entityId);
        log.setTimestamp(LocalDateTime.now());
        log.setPerformedBy("SYSTEM");
        log.setDetails(details);
        
        auditRepository.save(log);
    }
    
    // VIOLATION: Code Sustainability - No pagination for audit logs
    public List<AuditLog> getAllAuditLogs() {
        return auditRepository.findAll();  // Could be millions of logs
    }
    
    // VIOLATION: Code Sustainability - No index on action field
    public List<AuditLog> getAuditsByAction(String action) {
        return auditRepository.findByAction(action);  // Full table scan
    }
}

// Utility class with violations
@Service
public class QueryUtilityService {
    
    // VIOLATION: Code Sustainability - Database query utility with hardcoded params
    public Map<String, Object> buildTestQuery(String testName) {
        Map<String, Object> queryParams = new HashMap<>();
        
        // VIOLATION: Cloud Compatibility - Hardcoded database host
        String dbHost = "localhost";  // Should be configurable
        String dbPort = "5432";
        
        queryParams.put("dbHost", dbHost);
        queryParams.put("dbPort", dbPort);
        queryParams.put("testName", testName);
        
        return queryParams;
    }
}
