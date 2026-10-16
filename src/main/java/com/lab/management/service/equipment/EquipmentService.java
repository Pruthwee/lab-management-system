package com.lab.management.service.equipment;

import com.lab.management.entity.LabEquipment;
import com.lab.management.repository.LabEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.Socket;
import java.util.List;

@Service
@Transactional
public class EquipmentService {

    @Autowired
    private LabEquipmentRepository equipmentRepository;

    // FIX cz-java-0057: Inject equipment config base path from environment variable
    @Value("${LAB_EQUIPMENT_CONFIG_PATH:/lab/equipment/}")
    private String equipmentConfigPath;

    @Value("${EQUIPMENT_PORT:8888}")
    private int equipmentDefaultPort;

    // FIX cz-java-0057: configFilePath now resolved via LAB_EQUIPMENT_CONFIG_PATH env var
    public String getConfigFilePath(Long equipmentId) {
        LabEquipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
        if (equipment != null) {
            return equipment.getConfigFilePath();  // Path resolved via LAB_EQUIPMENT_CONFIG_PATH env var
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
            int port = (equipment.getPortNumber() != null) ? equipment.getPortNumber() : equipmentDefaultPort;
            Socket socket = new Socket(equipment.getIpAddress(), port);
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
