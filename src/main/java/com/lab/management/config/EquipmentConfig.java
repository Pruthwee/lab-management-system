package com.lab.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EquipmentConfig {

    // VIOLATION: Cloud Compatibility - Hardcoded equipment IPs
    @Bean
    public String getEquipmentIP() {
        return "192.168.1.100";  // Hardcoded IP
    }

    // VIOLATION: Cloud Compatibility - Hardcoded port numbers
    @Bean
    public Integer getEquipmentPort() {
        return 8888;  // Hardcoded port
    }
}
