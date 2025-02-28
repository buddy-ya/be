package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Point;
import com.team.buddyya.student.domain.PointStatus;
import com.team.buddyya.student.domain.PointType;
import com.team.buddyya.student.domain.Student;
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
        PointStatus intialPointStatus = PointStatus.builder()
                .point(point)
                .pointType(PointType.INITIAL)
                .changedPoint(PointType.INITIAL.getPointChange())
                .build();
        pointStatusRepository.save(intialPointStatus);
        PointStatus signUpPointStatus = PointStatus.builder()
                .point(point)
                .pointType(PointType.SIGNUP)
                .changedPoint(PointType.SIGNUP.getPointChange())
                .build();
        pointStatusRepository.save(signUpPointStatus);
    }

    public void updatePoint(Student student, String pointTypeValue){
        Point point = getPoint(student);
        PointType pointType = PointType.fromValue(pointTypeValue);
        int pointChange = pointType.getPointChange();
        point.updatePoint(pointChange);
        pointRepository.save(point);
        PointStatus pointStatus = PointStatus.builder()
                .point(point)
                .pointType(pointType)
                .changedPoint(pointChange)
                .build();
        pointStatusRepository.save(pointStatus);
    }

    public Point getPoint(Student student){
        return pointRepository.findByStudent(student)
                .orElseGet(() -> Point.builder().student(student).currentPoint(0).build());
    }
}
