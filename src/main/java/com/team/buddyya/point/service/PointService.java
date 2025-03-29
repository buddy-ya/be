package com.team.buddyya.point.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
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
    private final RegisteredPhoneRepository registeredPhoneRepository;

    public Point createPoint(Student student) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        boolean isDeleted = registeredPhone.getIsDeleted();
        PointType pointType = isDeleted ? PointType.NO_POINT_CHANGE : PointType.SIGNUP;
        Point point = createAndSavePoint(student, pointType);
        createAndSavePointStatus(point, pointType);
        if (isDeleted) {
            registeredPhone.updateIsDeleted(false);
        }
        return point;
    }

    private Point createAndSavePoint(Student student, PointType pointType) {
        Point point = Point.builder()
                .student(student)
                .currentPoint(pointType.getPointChange())
                .build();
        return pointRepository.save(point);
    }

    private void createAndSavePointStatus(Point point, PointType pointType) {
        PointStatus pointStatus = PointStatus.builder()
                .point(point)
                .pointType(pointType)
                .changedPoint(pointType.getPointChange())
                .build();
        pointStatusRepository.save(pointStatus);
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
