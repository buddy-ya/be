package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Table(name = "student")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 64, nullable = false)
    private String major;

    @Column(length = 64, nullable = false)
    private String country;

    @Column(length = 11, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean certificated;

    @Column(nullable = false)
    private Boolean korean;


    public Student(String name, String major, String country, Boolean certificated, Boolean korean) {
        this.name = name;
        this.major = major;
        this.country = country;
        this.certificated = certificated;
        this.korean = korean;
    }
}
