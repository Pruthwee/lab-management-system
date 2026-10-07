package com.lab.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CachingConfig {

    // VIOLATION: Code Sustainability - No distributed caching configured
    // Using local in-memory cache which won't work in cluster
    @Bean
    public com.google.common.cache.LoadingCache<String, Object> getTestCache() {
        return com.google.common.cache.CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(new com.google.common.cache.CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) {
                        return null;  // Simple in-memory cache
                    }
                });
    }
}
