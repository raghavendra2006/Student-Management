package com.student.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    // 1. Configure Dotenv to NOT crash if the file is missing
    private final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() 
            .load();

    @Bean
    public S3Client s3Client() {
        // 2. Try getting from Dotenv; if null, get from System Environment (Jenkins)
        String accessKey = getEnv("AWS_ACCESS_KEY_ID");
        String secretKey = getEnv("AWS_SECRET_ACCESS_KEY");
        String region = getEnv("AWS_REGION");

        // Simple validation to help you debug if something is still missing
        if (accessKey == null || secretKey == null || region == null) {
            throw new RuntimeException("Missing AWS Credentials! Check your .env or Jenkins environment variables.");
        }

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    // Helper method to check both sources
    private String getEnv(String key) {
        String value = dotenv.get(key);
        return (value != null) ? value : System.getenv(key);
    }
}