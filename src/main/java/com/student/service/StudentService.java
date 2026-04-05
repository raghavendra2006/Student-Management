package com.student.service;

import com.student.model.Student;
import com.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final S3Service s3Service;

    // ✅ CREATE
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    // ✅ READ ALL
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // ✅ READ BY ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    // ✅ UPDATE
    public Student updateStudent(Long id, Student updatedStudent, MultipartFile file) throws IOException {

        Student existingStudent = getStudentById(id);

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setRollNumber(updatedStudent.getRollNumber());
        existingStudent.setBranch(updatedStudent.getBranch());
        existingStudent.setCollege(updatedStudent.getCollege());
        existingStudent.setSkills(updatedStudent.getSkills());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setPhone(updatedStudent.getPhone());
        existingStudent.setAge(updatedStudent.getAge());
        existingStudent.setGender(updatedStudent.getGender());

        // 🔥 IMAGE UPDATE
        if (file != null && !file.isEmpty()) {

            if (existingStudent.getImageUrl() != null) {
                s3Service.deleteFile(existingStudent.getImageUrl());
            }

            String imageUrl = s3Service.uploadFile(file);
            existingStudent.setImageUrl(imageUrl);
        }

        return studentRepository.save(existingStudent);
    }

    // ✅ DELETE
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);

        if (student.getImageUrl() != null) {
            s3Service.deleteFile(student.getImageUrl());
        }

        studentRepository.deleteById(id);
    }

    // 🔥 FILTER + PAGINATION + SORT
    public Page<Student> getStudents(String skills, String gender, Integer age,
                                     int page, int size, String sort) {

        // 🔥 SKILLS → TOP 3 BASED ON COUNT
        if ("skills".equals(sort)) {

            List<Student> students = studentRepository.findAll();

            students.sort((a, b) -> {
                int countA = a.getSkills() != null ? a.getSkills().split(",").length : 0;
                int countB = b.getSkills() != null ? b.getSkills().split(",").length : 0;
                return Integer.compare(countB, countA);
            });

            List<Student> top3 = students.stream().limit(3).toList();

            return new PageImpl<>(top3);
        }

        Pageable pageable;

        // 🔥 SORTING
        if ("ageAsc".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("age").ascending());
        } else if ("ageDesc".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("age").descending());
        } else if ("name".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        } else if ("gender".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("gender").ascending());
        } else {
            pageable = PageRequest.of(page, size);
        }

        // 🔥 FILTERS (optional)
        if (gender != null && !gender.isEmpty()) {
            return studentRepository.findByGenderIgnoreCase(gender, pageable);
        }

        if (age != null) {
            return studentRepository.findByAge(age, pageable);
        }

        return studentRepository.findAll(pageable);
    }
}