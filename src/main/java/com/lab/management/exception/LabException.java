package com.lab.management.exception;

public class LabException extends RuntimeException {

    private String errorCode;
    private String details;

    public LabException(String message) {
        super(message);
        this.errorCode = "LAB_ERROR";
    }

    public LabException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "LAB_ERROR";
    }

    public LabException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LabException(String errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public LabException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
