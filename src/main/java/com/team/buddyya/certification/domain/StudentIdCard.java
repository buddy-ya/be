package com.team.buddyya.certification.domain;

import com.team.buddyya.common.domain.CreatedTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "student_id_card")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class StudentIdCard extends CreatedTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public StudentIdCard(String imageUrl, Student student) {
        this.imageUrl = imageUrl;
        this.student = student;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
