package com.lab.management.service.storage;

import com.lab.management.exception.StorageException;

public interface IStorageProvider {

    /**
     * Upload file to storage
     * VIOLATION: Cloud Compatibility - Currently local file system only
     */
    void uploadFile(String key, byte[] data) throws StorageException;

    /**
     * Download file from storage
     */
    byte[] downloadFile(String key) throws StorageException;

    /**
     * Get file URL for access
     */
    String getFileUrl(String key) throws StorageException;

    /**
     * Delete file from storage
     */
    void deleteFile(String key) throws StorageException;

    /**
     * Check if file exists
     */
    boolean fileExists(String key) throws StorageException;
}
