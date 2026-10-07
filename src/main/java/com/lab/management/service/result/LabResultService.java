package com.lab.management.service.result;

import com.lab.management.entity.LabResult;
import com.lab.management.repository.LabResultRepository;
import com.lab.management.repository.LabTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
