package com.lab.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class LabManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabManagementApplication.class, args);
        System.out.println("================================");
        System.out.println("Laboratory Management System Started");
        System.out.println("================================");
        System.out.println("API Documentation: http://localhost:8080/");
        System.out.println("Lab Results: http://localhost:8080/api/results");
        System.out.println("Specimens: http://localhost:8080/api/specimens");
        System.out.println("Equipment: http://localhost:8080/api/equipment");
        System.out.println("================================");
    }
}
