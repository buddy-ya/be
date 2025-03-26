package com.team.buddyya.point.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointStatus;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.dto.PointListResponse;
import com.team.buddyya.point.dto.PointResponse;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.point.repository.PointRepository;
import com.team.buddyya.point.repository.PointStatusRepository;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final PointRepository pointRepository;
    private final PointStatusRepository pointStatusRepository;
    private final FindStudentService findStudentService;
    private final FindPointService findPointService;

    public Point createPoint(Student student) {
        Point point = Point.builder()
                .student(student)
                .currentPoint(PointType.SIGNUP.getPointChange())
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

    public PointListResponse getPoints(StudentInfo studentInfo){
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Point point = findPointService.findByStudent(student);
        List<PointStatus> pointStatuses = pointStatusRepository.findAllByPointOrderByCreatedDateDesc(point);
        List<PointResponse> pointResponses = pointStatuses.stream()
                .map(PointResponse::from)
                .collect(Collectors.toList());
        return PointListResponse.from(point, pointResponses);
    }
}
