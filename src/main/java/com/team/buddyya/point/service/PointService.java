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
    private final FindPointService findPointService;
    private final FindStudentService findStudentService;
    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final UpdatePointService updatePointService;

    public Point createPoint(Student student) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        boolean hasWithdrawn = registeredPhone.getHasWithdrawn();
        PointType pointType = hasWithdrawn ? PointType.NO_POINT_CHANGE : PointType.NEW_SIGNUP;
        Point point = createAndSavePoint(student, pointType, hasWithdrawn);
        if (!hasWithdrawn) {
            return rewardFestivalPoint(student);
        }
        return point;
    }

    private Point createAndSavePoint(Student student, PointType pointType, boolean hasWithdrawn) {
        Point point = Point.builder()
                .student(student)
                .currentPoint(pointType.getPointChange())
                .build();
        pointRepository.save(point);
        if (!hasWithdrawn) {
            PointStatus pointStatus = PointStatus.builder()
                    .point(point)
                    .pointType(pointType)
                    .changedPoint(pointType.getPointChange())
                    .build();
            pointStatusRepository.save(pointStatus);
        }
        return point;
    }

    public PointListResponse getPoints(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Point point = findPointService.findByStudent(student);
        List<PointStatus> pointStatuses = pointStatusRepository.findAllByPointOrderByCreatedDateDesc(point);
        List<PointResponse> pointResponses = pointStatuses.stream()
                .map(PointResponse::from)
                .collect(Collectors.toList());
        return PointListResponse.from(point, pointResponses);
    }

    private Point rewardFestivalPoint(Student student) {
        return updatePointService.updatePoint(student, PointType.FESTIVAL_REWARD);
    }
}
