package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "university")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class University {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String universityName;

    @Column(name = "active", nullable = false)
    private Boolean isActive;

    @Builder
    public University(String universityName) {
        this.universityName = universityName;
        isActive = true;
    }
}