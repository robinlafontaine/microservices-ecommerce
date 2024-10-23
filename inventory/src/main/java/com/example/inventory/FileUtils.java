package com.example.inventory;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    /**
     * Converts a file path to a MultipartFile.
     *
     * @param filePath the path to the file
     * @return MultipartFile corresponding to the given file path
     * @throws IOException if an I/O error occurs
     */
    public static MultipartFile convertFileToMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        byte[] fileContent = Files.readAllBytes(file.toPath());

        return new MockMultipartFile(
                "file",
                file.getName(),
                "application/octet-stream",
                fileContent
        );
    }
}