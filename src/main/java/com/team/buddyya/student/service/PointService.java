package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Point;
import com.team.buddyya.student.domain.PointStatus;
import com.team.buddyya.student.domain.PointType;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.PointRepository;
import com.team.buddyya.student.repository.PointStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private static final int INITIAL_POINT = 1;

    private final PointRepository pointRepository;
    private final PointStatusRepository pointStatusRepository;

    public void createPoint(Student student) {
        Point point = Point.builder()
                .student(student)
                .currentPoint(INITIAL_POINT)
                .build();
        pointRepository.save(point);
        PointStatus signUpPointStatus = PointStatus.builder()
                .point(point)
                .pointType(PointType.SIGNUP)
                .changedPoint(PointType.SIGNUP.getPointChange())
                .build();
        pointStatusRepository.save(signUpPointStatus);
    }

    public Point updatePoint(Student student, PointType pointType){
        Point point = getPoint(student);
        int pointChange = pointType.getPointChange();
        point.updatePoint(pointChange);
        PointStatus pointStatus = PointStatus.builder()
                .point(point)
                .pointType(pointType)
                .changedPoint(pointChange)
                .build();
        pointStatusRepository.save(pointStatus);
        return point;
    }

    public Point getPoint(Student student) {
        return pointRepository.findByStudent(student)
                .orElseThrow(() -> new StudentException(StudentExceptionType.POINT_NOT_FOUND));
    }
}
