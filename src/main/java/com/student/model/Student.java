package com.student.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String rollNumber;
    private String branch;
    private String college;

    @Column(length = 1000)
    private String skills;

    private String email;
    private String phone;
    private Integer age;
    private String gender;

    private String imageUrl;
}