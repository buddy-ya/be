package com.team.buddyya.student.domain;

import jakarta.persistence.*;
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

    @Column(length = 255, nullable = false)
    private String universityName;

    @Column
    private Long count;

    public University(String universityName, Long count) {
        this.universityName = universityName;
        this.count = count;
    }
}