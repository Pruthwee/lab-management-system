package com.lab.management.service.storage;

import com.lab.management.exception.StorageException;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class LocalStorageProvider implements IStorageProvider {

    // VIOLATION: Software Portability - Windows-specific path
    private static final String BASE_PATH = "C:\\lab\\specimens\\";

    @Override
    public void uploadFile(String key, byte[] data) throws StorageException {
        try {
            // Ensure directory exists
            File dir = new File(BASE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Write file
            String filePath = BASE_PATH + key;
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            throw new StorageException("LOCAL", key, "Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadFile(String key) throws StorageException {
        try {
            String filePath = BASE_PATH + key;
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            throw new StorageException("LOCAL", key, "Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileUrl(String key) throws StorageException {
        // VIOLATION: Cloud Compatibility - Returns local file path instead of URL
        return "file://" + BASE_PATH + key;
    }

    @Override
    public void deleteFile(String key) throws StorageException {
        try {
            String filePath = BASE_PATH + key;
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            throw new StorageException("LOCAL", key, "Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String key) throws StorageException {
        try {
            String filePath = BASE_PATH + key;
            return Files.exists(Paths.get(filePath));
        } catch (Exception e) {
            throw new StorageException("LOCAL", key, "Failed to check file existence: " + e.getMessage(), e);
        }
    }
}
