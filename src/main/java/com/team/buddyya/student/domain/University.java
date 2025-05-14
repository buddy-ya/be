package com.team.buddyya.student.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "match_active", nullable = false)
    private Boolean isMatchingActive;

    @Column(name = "feed_active", nullable = false)
    private Boolean isFeedActive;

    @Builder
    public University(String universityName) {
        this.universityName = universityName;
        isActive = true;
        isMatchingActive = true;
        isFeedActive = false;
    }
}
