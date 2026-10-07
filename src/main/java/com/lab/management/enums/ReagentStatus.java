package com.lab.management.enums;

public enum ReagentStatus {
    ACTIVE("Active"),
    EXPIRED("Expired"),
    LOW_STOCK("Low Stock"),
    DISCONTINUED("Discontinued");

    private final String displayName;

    ReagentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReagentStatus fromString(String value) {
        if (value == null) return ACTIVE;
        try {
            return ReagentStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
