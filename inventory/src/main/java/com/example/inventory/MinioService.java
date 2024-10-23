package com.example.inventory;

import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucketName;

    private MinioClient minioClient;


    @PostConstruct
    public void initializeMinio() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                String policyJson = "{\"Version\": \"2012-10-17\",\"Statement\": [{\"Effect\": \"Allow\",\"Principal\": \"*\",\"Action\": \"s3:GetObject\",\"Resource\": \"arn:aws:s3:::product-images/*\"}]}";
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder().bucket("product-images").config(policyJson).build());
                System.out.println("Bucket '" + bucketName + "' created successfully.");
            } else {
                System.out.println("Bucket '" + bucketName + "' already exists.");
            }
        } catch (MinioException | IOException | IllegalArgumentException | InvalidKeyException |
                 NoSuchAlgorithmException e) {
            throw new RuntimeException("Error initializing MinIO: " + e.getMessage(), e);
        }
    }

    public String uploadImage(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        return "http://localhost:9000/" + bucketName + "/" + fileName;
    }
}

