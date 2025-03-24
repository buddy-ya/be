package com.team.buddyya.point.service;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointStatus;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.point.repository.PointRepository;
import com.team.buddyya.point.repository.PointStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private static final int INITIAL_POINT = 100;

    private final PointRepository pointRepository;
    private final PointStatusRepository pointStatusRepository;

    public Point createPoint(Student student) {
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
        return point;
    }
}
