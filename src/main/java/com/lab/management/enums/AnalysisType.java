package com.lab.management.enums;

public enum AnalysisType {
    QUANTITATIVE("Quantitative"),
    QUALITATIVE("Qualitative"),
    SEMI_QUANTITATIVE("Semi-Quantitative");

    private final String displayName;

    AnalysisType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AnalysisType fromString(String value) {
        if (value == null) return QUANTITATIVE;
        try {
            return AnalysisType.valueOf(value.toUpperCase().replace(" ", "_").replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return QUANTITATIVE;
        }
    }
}
