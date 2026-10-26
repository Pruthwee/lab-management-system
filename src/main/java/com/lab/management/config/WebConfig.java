package com.lab.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    // VIOLATION: Cloud Compatibility - Hardcoded API URLs
    @Bean
    public String getApiBaseUrl() {
        return "http://localhost:8080";  // Hardcoded URL
    }

    // VIOLATION: Software Portability - Windows-specific file paths
    @Bean
    public String getLabDataPath() {
        return System.getenv().getOrDefault("DATA_PATH", "/app/data") + "/";  // Resolved from environment
    }
}
