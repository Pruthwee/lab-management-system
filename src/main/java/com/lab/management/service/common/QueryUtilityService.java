package com.lab.management.service.common;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Build query parameters
     */
    public Map<String, Object> buildQuery(String tableName, Map<String, Object> filters) {
        Map<String, Object> params = new HashMap<>();
        params.put("table", tableName);
        params.putAll(filters);
        return params;
    }
}
