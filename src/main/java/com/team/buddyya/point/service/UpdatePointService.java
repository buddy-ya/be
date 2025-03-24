package com.team.buddyya.point.service;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointStatus;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.repository.PointStatusRepository;
import com.team.buddyya.student.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePointService {

    private final FindPointService findPointService;
    private final PointStatusRepository pointStatusRepository;

    public Point updatePoint(Student student, PointType pointType){
        Point point = findPointService.findByStudent(student);
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
}
