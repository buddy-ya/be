package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "student")
@Getter
@NoArgsConstructor(access = PROTECTED)
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

    @Column(nullable = false)
    private Boolean certificated;

    @Column(nullable = false)
    private Boolean korean;

    @OneToOne(mappedBy = "student", fetch = LAZY)
    private Avatar avatar;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @OneToMany(mappedBy = "student")
    private List<StudentLanguage> languages;

    @OneToMany(mappedBy = "student")
    private List<StudentInterest> interests;

    @Builder
    public Student(String name, String major, String country, Boolean korean, Role role, University university, Gender gender) {
        this.name = name;
        this.major = major;
        this.country = country;
        this.korean = korean;
        this.role = role;
        this.university = university;
        this.gender = gender;
        this.certificated = false;
    }
}