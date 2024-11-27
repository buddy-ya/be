package com.team.buddyya.student.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "profile_image")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ProfileImage extends CreatedTime {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(length = 255, nullable = false)
    private String url;

    @Builder
    public ProfileImage(Student student, String url) {
        this.student = student;
        this.url = url;
    }

    public void updateUrl(String newUrl) {
        this.url = newUrl;
    }
}
