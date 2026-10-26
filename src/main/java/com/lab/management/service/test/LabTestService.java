package com.lab.management.service.test;

import com.lab.management.entity.LabTest;
import com.lab.management.repository.LabTestRepository;
import com.lab.management.repository.LabEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        String equipmentIP = System.getenv().getOrDefault("EQUIPMENT_IP", "192.168.1.100");  // Hardcoded from entity
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
