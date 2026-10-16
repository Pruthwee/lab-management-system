package com.lab.management.service.test;

import com.lab.management.entity.LabTest;
import com.lab.management.repository.LabTestRepository;
import com.lab.management.repository.LabEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LabTestService {

    @Autowired
    private LabTestRepository testRepository;

    @Autowired
    private LabEquipmentRepository equipmentRepository;

    @Value("${EQUIPMENT_IP:equipment-service}")
    private String equipmentIP;

    @Value("${EQUIPMENT_PORT:8888}")
    private int equipmentPort;

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
    public List<LabTest> getTestsInDateRange(LocalDateTime start, LocalDateTime end) {
        return testRepository.findByCreatedDateBetween(start, end);  // Could be millions
    }

    // VIOLATION: Cloud Compatibility - Hardcoded IP address access
    public void connectToEquipment() {
        // Fixed: IP and port now injected via environment variables EQUIPMENT_IP and EQUIPMENT_PORT
        // Uses equipmentIP and equipmentPort fields
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
