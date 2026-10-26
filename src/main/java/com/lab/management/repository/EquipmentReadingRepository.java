package com.lab.management.repository;

import com.lab.management.entity.EquipmentReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// VIOLATION: Cloud Compatibility - Hardcoded server connection in entity
@Repository
public interface EquipmentReadingRepository extends JpaRepository<EquipmentReading, Long> {
    
    List<EquipmentReading> findByEquipmentEquipmentId(Long equipmentId);
}
