package com.team.buddyya.student.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "student_interest")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class StudentInterest extends CreatedTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    @Builder
    public StudentInterest(Student student, Interest interest) {
        this.student = student;
        this.interest = interest;
    }

    public void setStudent(Student student) {
        this.student = student;
        student.getInterests().add(this);
    }

    @Override
    public String toString() {
        return interest.getInterestName();
    }
}
