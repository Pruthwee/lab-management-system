package com.lab.management.service.equipment;

import com.lab.management.entity.LabEquipment;
import com.lab.management.repository.LabEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.Socket;
import java.util.List;

@Service
@Transactional
public class EquipmentService {

    @Autowired
    private LabEquipmentRepository equipmentRepository;

    // VIOLATION: Software Portability - Windows-specific path handling
    public String getConfigFilePath(Long equipmentId) {
        LabEquipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        if (equipment != null) {
            return equipment.getConfigFilePath();  // e.g., "/app/data/equipment/config.xml" from DATA_PATH
        }
        return null;
    }

    // VIOLATION: Code Sustainability - String status instead of enum
    public List<LabEquipment> getActiveEquipment() {
        return equipmentRepository.findByStatus("Active");
    }

    // VIOLATION: Cloud Compatibility - Hardcoded IP connectivity
    public boolean testEquipmentConnection(Long equipmentId) {
        LabEquipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        if (equipment == null) return false;

        // VIOLATION: Cloud Compatibility - Hardcoded IP:Port
        try {
            Socket socket = new Socket(equipment.getIpAddress(), equipment.getPortNumber());
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // VIOLATION: Code Sustainability - BLOB storage instead of external reference
    public void storeCalibrationData(Long equipmentId, String calibrationJson) {
        LabEquipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        if (equipment != null) {
            // VIOLATION: Database Dependency - CLOB storage in database
            equipment.setCalibrationData(calibrationJson);
            equipmentRepository.save(equipment);
        }
    }
}
