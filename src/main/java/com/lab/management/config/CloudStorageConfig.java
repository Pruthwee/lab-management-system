package com.lab.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.lab.management.service.storage.IStorageProvider;
import com.lab.management.service.storage.LocalStorageProvider;

@Configuration
public class CloudStorageConfig {

    // VIOLATION: Cloud Compatibility - Currently using local storage only
    // Future implementation should support S3/Azure/GCS
    @Bean
    @Primary
    public IStorageProvider storageProvider() {
        // Using LocalStorageProvider for now (backward compatible)
        // Can be swapped with S3StorageProvider or AzureBlobStorageProvider
        return new LocalStorageProvider();
    }
}
