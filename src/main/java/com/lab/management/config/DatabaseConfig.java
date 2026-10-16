package com.lab.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    // Migrated from file-based H2 to network-accessible PostgreSQL via environment variables
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl(System.getenv().getOrDefault("DB_URL",
                "jdbc:postgresql://" + System.getenv().getOrDefault("DB_HOST", "localhost")
                + ":" + System.getenv().getOrDefault("DB_PORT", "5432")
                + "/" + System.getenv().getOrDefault("DB_NAME", "labdb")));
        properties.setUsername(System.getenv().getOrDefault("DB_USERNAME", "postgres"));
        properties.setPassword(System.getenv().getOrDefault("DB_PASSWORD", ""));
        return properties;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        // Migrated from file-based H2 (org.h2.Driver) to network-accessible PostgreSQL
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(System.getenv().getOrDefault("DB_URL",
                        "jdbc:postgresql://" + System.getenv().getOrDefault("DB_HOST", "localhost")
                        + ":" + System.getenv().getOrDefault("DB_PORT", "5432")
                        + "/" + System.getenv().getOrDefault("DB_NAME", "labdb")))
                .username(System.getenv().getOrDefault("DB_USERNAME", "postgres"))
                .password(System.getenv().getOrDefault("DB_PASSWORD", ""))
                .build();
    }
}
