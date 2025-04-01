package com.team.buddyya.certification.domain;

import com.team.buddyya.common.domain.BaseTime;
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
public class StudentIdCard extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Builder
    public StudentIdCard(String imageUrl, Student student) {
        this.imageUrl = imageUrl;
        this.student = student;
        this.rejectionReason = null;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setStudent(Student student) {
        this.student = student;
        student.setStudentIdCard(this);
    }
}
