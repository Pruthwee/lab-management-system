package com.lab.management.repository;

import com.lab.management.entity.LabEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// VIOLATION: Cloud Compatibility - Using custom SQL with hardcoded database host
@Repository
public interface LabEquipmentRepository extends JpaRepository<LabEquipment, Long> {

    // VIOLATION: Database Dependency - SQL Server DATEPART if this were real
    List<LabEquipment> findByStatus(String status);

    // VIOLATION: Cloud Compatibility - IP address lookup (hardcoded in entity)
    Optional<LabEquipment> findByIpAddress(String ipAddress);
}
