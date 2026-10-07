package com.lab.management.enums;

public enum QualityCheckStatus {
    PASS("Pass"),
    FAIL("Fail"),
    CONDITIONAL_PASS("Conditional Pass");

    private final String displayName;

    QualityCheckStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static QualityCheckStatus fromString(String value) {
        if (value == null) return PASS;
        try {
            return QualityCheckStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return PASS;
        }
    }
}
