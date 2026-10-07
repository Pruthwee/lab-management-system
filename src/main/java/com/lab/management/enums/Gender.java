package com.lab.management.enums;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
    UNDISCLOSED("Undisclosed");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromString(String value) {
        if (value == null) return UNDISCLOSED;
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNDISCLOSED;
        }
    }
}
