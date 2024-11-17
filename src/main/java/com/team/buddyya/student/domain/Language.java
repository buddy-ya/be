package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "language")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Language {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String languageName;

    @Builder
    public Language(String languageName) {
        this.languageName = languageName;
    }
}