package com.student.Student_Management;

import com.student.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
class StudentManagementApplicationTests {

    @MockBean
    private S3Client s3Client;

    @MockBean
    private S3Service s3Service;

    @Test
    void contextLoads() {
    }
}
