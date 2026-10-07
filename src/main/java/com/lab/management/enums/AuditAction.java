package com.lab.management.enums;

public enum AuditAction {
    CREATE("Create"),
    READ("Read"),
    UPDATE("Update"),
    DELETE("Delete"),
    EXPORT("Export");

    private final String displayName;

    AuditAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AuditAction fromString(String value) {
        if (value == null) return READ;
        try {
            return AuditAction.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return READ;
        }
    }
}
