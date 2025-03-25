package com.team.buddyya.point.domain;

import com.team.buddyya.common.domain.BaseTime;
import com.team.buddyya.point.exception.PointException;
import com.team.buddyya.point.exception.PointExceptionType;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseTime {

    private static final int MIN_POINT = 0;
    private static final int MAX_POINT = 100_000;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(name = "current_point", nullable = false)
    private Integer currentPoint;

    @Builder
    public Point(Student student, int currentPoint) {
        this.student = student;
        this.currentPoint = currentPoint;
    }

    public void updatePoint(int pointChange) {
        int newPoint = this.currentPoint + pointChange;
        validatePoint(newPoint);
        this.currentPoint = newPoint;
    }

    private void validatePoint(int point) {
        if (point < MIN_POINT) {
            throw new PointException(PointExceptionType.NEGATIVE_POINT);
        }
        if (point > MAX_POINT) {
            throw new PointException(PointExceptionType.EXCEED_MAX_POINT);
        }
    }
}
