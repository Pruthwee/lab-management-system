package com.lab.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    // VIOLATION: Cloud Compatibility - Hardcoded API URLs
    @Bean
    public String getApiBaseUrl() {
        return "http://localhost:8080";  // Hardcoded URL
    }

    // FIX cz-java-0057: Replaced hardcoded Windows absolute path with environment variable
    @Value("${LAB_DATA_PATH:/lab/data/}")
    private String labDataPath;

    @Bean
    public String getLabDataPath() {
        return labDataPath;
    }
}
