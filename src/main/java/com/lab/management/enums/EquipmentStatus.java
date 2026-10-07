package com.lab.management.enums;

public enum EquipmentStatus {
    ACTIVE("Active"),
    MAINTENANCE("Maintenance"),
    OFFLINE("Offline"),
    CALIBRATION("Calibration");

    private final String displayName;

    EquipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EquipmentStatus fromString(String value) {
        if (value == null) return ACTIVE;
        try {
            return EquipmentStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
