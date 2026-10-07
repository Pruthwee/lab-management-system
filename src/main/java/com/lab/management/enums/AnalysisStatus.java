package com.lab.management.enums;

public enum AnalysisStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    APPROVED("Approved");

    private final String displayName;

    AnalysisStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AnalysisStatus fromString(String value) {
        if (value == null) return PENDING;
        try {
            return AnalysisStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
