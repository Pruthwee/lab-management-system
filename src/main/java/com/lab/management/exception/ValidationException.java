package com.lab.management.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends LabException {

    private Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", "Validation failed: " + message);
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, message);
    }

    public ValidationException(Map<String, String> fieldErrors) {
        super("VALIDATION_ERROR", "Multiple validation errors occurred");
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String field, String message) {
        if (fieldErrors == null) {
            fieldErrors = new HashMap<>();
        }
        fieldErrors.put(field, message);
    }
}
