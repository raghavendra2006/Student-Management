package com.student.repository;

import com.student.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findBySkillsContainingIgnoreCase(String skills, Pageable pageable);

    Page<Student> findByGenderIgnoreCase(String gender, Pageable pageable);

    Page<Student> findByAge(Integer age, Pageable pageable);
}