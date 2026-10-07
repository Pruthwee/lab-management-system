package com.lab.management.util;

import com.lab.management.exception.ValidationException;

public class ValidationUtils {

    /**
     * Validate specimen type
     * VIOLATION: Code Sustainability - No validation annotations used
     */
    public static void validateSpecimenType(String specimenType) throws ValidationException {
        if (specimenType == null || specimenType.trim().isEmpty()) {
            throw new ValidationException("specimenType", "Specimen type cannot be empty");
        }
    }

    /**
     * Validate email format
     * VIOLATION: Code Sustainability - Basic regex only
     */
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("email", "Invalid email format");
        }
    }

    /**
     * Validate medical record number
     * VIOLATION: Code Sustainability - No complex validation
     */
    public static void validateMRN(String mrn) throws ValidationException {
        if (mrn == null || mrn.trim().isEmpty()) {
            throw new ValidationException("mrn", "Medical record number cannot be empty");
        }
        if (mrn.length() < 5) {
            throw new ValidationException("mrn", "Medical record number must be at least 5 characters");
        }
    }

    /**
     * Validate test name
     */
    public static void validateTestName(String testName) throws ValidationException {
        if (testName == null || testName.trim().isEmpty()) {
            throw new ValidationException("testName", "Test name cannot be empty");
        }
    }

    /**
     * Validate date range
     */
    public static void validateDateRange(String startDate, String endDate) throws ValidationException {
        if (startDate == null || endDate == null) {
            throw new ValidationException("dateRange", "Start and end dates are required");
        }
    }

    /**
     * Validate specimen ID
     */
    public static void validateSpecimenId(Long specimenId) throws ValidationException {
        if (specimenId == null || specimenId <= 0) {
            throw new ValidationException("specimenId", "Invalid specimen ID");
        }
    }

    /**
     * Validate test ID
     */
    public static void validateTestId(Long testId) throws ValidationException {
        if (testId == null || testId <= 0) {
            throw new ValidationException("testId", "Invalid test ID");
        }
    }
}
