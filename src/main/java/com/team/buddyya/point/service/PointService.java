package com.team.buddyya.point.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointChangeType;
import com.team.buddyya.point.domain.PointStatus;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.dto.PointListResponse;
import com.team.buddyya.point.dto.PointMissionResponse;
import com.team.buddyya.point.dto.PointResponse;
import com.team.buddyya.point.dto.PointMissionRewardResponse;
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
    private final UpdatePointService updatePointService;
    private final PointStatusRepository pointStatusRepository;
    private final FindPointService findPointService;
    private final FindStudentService findStudentService;
    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final NotificationService notificationService;

    public Point createPoint(Student student) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        boolean hasWithdrawn = registeredPhone.getHasWithdrawn();
        PointType pointType = hasWithdrawn ? PointType.NO_POINT_CHANGE : PointType.SIGNUP;
        Point point = createAndSavePoint(student, pointType, hasWithdrawn);
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

    public PointMissionResponse getUserMissionInfo(StudentInfo studentInfo){
        Student student = findStudentService.findByStudentId(studentInfo.id());
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        Point point = findPointService.findByStudent(student);
        int totalMissionPoint = getTotalMissionPoint(point);
        return PointMissionResponse.from(student, registeredPhone, totalMissionPoint);
    }

    public PointMissionRewardResponse checkAttendanceAndReward(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        if (registeredPhone.isTodayAttended()) {
            return PointMissionRewardResponse.from(null, PointType.NO_POINT_CHANGE.getPointChange());
        }
        Point point = updatePointService.updatePoint(student, PointType.MISSION_VISIT_REWARD);
        registeredPhone.updateLastAttendanceDateToToday();
        return PointMissionRewardResponse.from(point.getCurrentPoint(), PointType.MISSION_VISIT_REWARD.getPointChange());
    }

    public int getTotalMissionPoint(Point point) {
        return pointStatusRepository.findAllByPointOrderByCreatedDateDesc(point).stream()
                .filter(ps -> ps.getPointType().getChangeType() == PointChangeType.MISSION)
                .mapToInt(PointStatus::getChangedPoint)
                .sum();
    }
}
