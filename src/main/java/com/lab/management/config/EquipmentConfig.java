package com.lab.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class EquipmentConfig {

    @Value("${EQUIPMENT_IP:equipment-service}")
    private String equipmentIP;

    // Fixed: Hardcoded IP replaced with environment variable EQUIPMENT_IP
    @Bean
    public String getEquipmentIP() {
        return equipmentIP;
    }

    // VIOLATION: Cloud Compatibility - Hardcoded port numbers
    @Bean
    public Integer getEquipmentPort() {
        return 8888;  // Hardcoded port
    }
}
