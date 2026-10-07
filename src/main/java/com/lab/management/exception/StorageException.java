package com.lab.management.exception;

public class StorageException extends LabException {

    private String storageType;
    private String fileKey;

    public StorageException(String message) {
        super("STORAGE_ERROR", message);
    }

    public StorageException(String message, Throwable cause) {
        super("STORAGE_ERROR", message, cause);
    }

    public StorageException(String storageType, String fileKey, String message) {
        super("STORAGE_ERROR", message);
        this.storageType = storageType;
        this.fileKey = fileKey;
    }

    public StorageException(String storageType, String fileKey, String message, Throwable cause) {
        super("STORAGE_ERROR", message, cause);
        this.storageType = storageType;
        this.fileKey = fileKey;
    }

    public String getStorageType() {
        return storageType;
    }

    public String getFileKey() {
        return fileKey;
    }
}
