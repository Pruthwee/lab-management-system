package com.lab.management.enums;

public enum SpecimenStatus {
    ACTIVE("Active"),
    PROCESSED("Processed"),
    ARCHIVED("Archived"),
    CONTAMINATED("Contaminated"),
    EXPIRED("Expired");

    private final String displayName;

    SpecimenStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SpecimenStatus fromString(String value) {
        if (value == null) return ACTIVE;
        try {
            return SpecimenStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
