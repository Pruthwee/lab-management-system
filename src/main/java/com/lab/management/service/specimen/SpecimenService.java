package com.lab.management.service.specimen;

import com.lab.management.entity.Specimen;
import com.lab.management.repository.SpecimenRepository;
import com.lab.management.repository.LabTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecimenService {

    @Autowired
    private SpecimenRepository specimenRepository;

    @Autowired
    private LabTestRepository testRepository;

    // FIX cz-java-0057: Inject specimen storage base path from environment variable
    @Value("${LAB_SPECIMENS_PATH:/lab/specimens/}")
    private String specimenStoragePath;

    // VIOLATION: Code Sustainability - No pagination, loads all specimens
    public List<Specimen> getAllSpecimens() {
        return specimenRepository.findAll();  // Could be millions of records
    }

    // VIOLATION: Cloud Compatibility - Using local file system path
    public void storeSpecimenLocally(Specimen specimen, byte[] imageData) {
        // FIX cz-java-0057: Use environment variable for specimen storage path
        String filePath = specimenStoragePath + specimen.getSpecimenId() + ".img";
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
