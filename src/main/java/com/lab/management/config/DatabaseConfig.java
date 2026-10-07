package com.lab.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    // VIOLATION: Cloud Compatibility - Hardcoded database host and port
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("jdbc:h2:mem:testdb;MODE=Oracle");
        properties.setUsername("sa");
        properties.setPassword("");
        return properties;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        // VIOLATION: Database Dependency - Oracle mode for H2
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb;MODE=Oracle;DATABASE_TO_UPPER=false")
                .username("sa")
                .password("")
                .build();
    }
}
