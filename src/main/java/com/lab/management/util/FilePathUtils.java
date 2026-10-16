package com.lab.management.util;

import java.io.File;
import java.nio.file.Paths;

public class FilePathUtils {

    // FIX cz-java-0057: Replaced hardcoded Windows absolute paths with environment variables
    // Paths are now injected via Kubernetes ConfigMap environment variables on EKS

    private static final String LAB_DATA_PATH = System.getenv("LAB_DATA_PATH") != null ? System.getenv("LAB_DATA_PATH") : "/lab/data/";
    private static final String LAB_BACKUP_PATH = System.getenv("LAB_BACKUP_PATH") != null ? System.getenv("LAB_BACKUP_PATH") : "/lab/backup/";
    private static final String LAB_REPORTS_PATH = System.getenv("LAB_REPORTS_PATH") != null ? System.getenv("LAB_REPORTS_PATH") : "/lab/reports/";

    /**
     * Get lab data path - resolved from LAB_DATA_PATH environment variable
     */
    public static String getLabDataPath() {
        return LAB_DATA_PATH;
    }

    /**
     * Get lab backup path - resolved from LAB_BACKUP_PATH environment variable
     */
    public static String getLabBackupPath() {
        return LAB_BACKUP_PATH;
    }

    /**
     * Get lab reports path - resolved from LAB_REPORTS_PATH environment variable
     */
    public static String getLabReportsPath() {
        return LAB_REPORTS_PATH;
    }

    /**
     * Get equipment config path - resolved from LAB_EQUIPMENT_CONFIG_PATH environment variable
     * FIX cz-java-0057: Replaced hardcoded Windows path with environment variable
     */
    public static String getEquipmentConfigPath(String filename) {
        String equipmentConfigPath = System.getenv("LAB_EQUIPMENT_CONFIG_PATH") != null ? System.getenv("LAB_EQUIPMENT_CONFIG_PATH") : "/lab/equipment/";
        return equipmentConfigPath + filename;
    }

    /**
     * Get specimen storage path
     */
    // FIX cz-java-0067: Replaced platform-specific File.separator with cross-platform Paths.get() (Java NIO)
    public static String getSpecimenPath(Long specimenId) {
        return Paths.get(LAB_DATA_PATH, "specimens", specimenId + ".img").toString();
    }

    /**
     * Ensure directory exists
     */
    public static boolean ensureDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }
}
