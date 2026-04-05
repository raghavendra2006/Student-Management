package com.student.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    // Manual constructor to handle S3Client injection and Environment logic
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;

        // 1. Load Dotenv but IGNORE if the file is missing (Jenkins fallback)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 2. Get Bucket Name from .env OR System Environment (Jenkins)
        String bName = dotenv.get("AWS_S3_BUCKET");
        this.bucketName = (bName != null) ? bName : System.getenv("AWS_S3_BUCKET");

        // 3. Get Region from .env OR System Environment (Jenkins)
        String rName = dotenv.get("AWS_REGION");
        this.region = (rName != null) ? rName : System.getenv("AWS_REGION");
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Validation check
        if (bucketName == null) {
            throw new RuntimeException("AWS_S3_BUCKET variable is not set!");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );

        // Uses the dynamic region for the URL
        return "https://" + bucketName + ".s3." + (region != null ? region : "us-east-1") + ".amazonaws.com/" + fileName;
    }

    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}