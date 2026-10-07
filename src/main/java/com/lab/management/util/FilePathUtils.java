package com.lab.management.util;

import java.io.File;

public class FilePathUtils {

    // VIOLATION: Software Portability - Windows-specific paths
    // This utility should eventually use File.separator and Paths.get()

    private static final String LAB_DATA_PATH = "C:\\lab\\data\\";
    private static final String LAB_BACKUP_PATH = "C:\\lab\\backup\\";
    private static final String LAB_REPORTS_PATH = "C:\\lab\\reports\\";

    /**
     * Get lab data path (VIOLATION: Windows-only)
     */
    public static String getLabDataPath() {
        return LAB_DATA_PATH;
    }

    /**
     * Get lab backup path (VIOLATION: Windows-only)
     */
    public static String getLabBackupPath() {
        return LAB_BACKUP_PATH;
    }

    /**
     * Get lab reports path (VIOLATION: Windows-only)
     */
    public static String getLabReportsPath() {
        return LAB_REPORTS_PATH;
    }

    /**
     * Get equipment config path (VIOLATION: Windows-only)
     */
    public static String getEquipmentConfigPath(String filename) {
        return "C:\\lab\\equipment\\" + filename;
    }

    /**
     * Get specimen storage path
     */
    public static String getSpecimenPath(Long specimenId) {
        return LAB_DATA_PATH + File.separator + "specimens" + File.separator + specimenId + ".img";
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
