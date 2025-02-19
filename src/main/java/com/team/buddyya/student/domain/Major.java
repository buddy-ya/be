package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "major")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Major {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String majorName;

    @Builder
    public Major(String majorName) {
        this.majorName = majorName;
    }
}
