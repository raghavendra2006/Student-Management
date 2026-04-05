package com.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.model.Student;
import com.student.service.S3Service;
import com.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final S3Service s3Service;

    // ✅ CREATE
    @PostMapping(consumes = "multipart/form-data")
    public Student addStudent(
            @RequestParam("student") String studentJson,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(studentJson, Student.class);

        String imageUrl = s3Service.uploadFile(file);
        student.setImageUrl(imageUrl);

        return studentService.addStudent(student);
    }

    // ✅ READ ALL
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // ✅ READ BY ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // ✅ UPDATE
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public Student updateStudent(
            @PathVariable Long id,
            @RequestParam("student") String studentJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Student updatedStudent = mapper.readValue(studentJson, Student.class);

        return studentService.updateStudent(id, updatedStudent, file);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully";
    }

    // 🔥 FILTER + PAGINATION + SORT
    @GetMapping("/filter")
    public Page<Student> getStudents(
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String sort
    ) {
        return studentService.getStudents(skills, gender, age, page, size, sort);
    }
}