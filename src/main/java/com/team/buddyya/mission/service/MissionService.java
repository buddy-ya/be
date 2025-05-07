package com.team.buddyya.mission.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.mission.exception.MissionException;
import com.team.buddyya.mission.exception.MissionExceptionType;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointChangeType;
import com.team.buddyya.point.domain.PointStatus;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.mission.dto.PointMissionResponse;
import com.team.buddyya.mission.dto.PointMissionRewardResponse;
import com.team.buddyya.point.repository.PointStatusRepository;
import com.team.buddyya.point.service.FindPointService;
import com.team.buddyya.point.service.UpdatePointService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MissionService {

    private final UpdatePointService updatePointService;
    private final PointStatusRepository pointStatusRepository;
    private final FindPointService findPointService;
    private final FindStudentService findStudentService;
    private final RegisteredPhoneRepository registeredPhoneRepository;

    public PointMissionResponse getUserMissionInfo(StudentInfo studentInfo){
        Student student = findStudentService.findByStudentId(studentInfo.id());
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        Point point = findPointService.findByStudent(student);
        int totalMissionPoint = getTotalMissionPoint(point);
        return PointMissionResponse.from(registeredPhone, totalMissionPoint);
    }

    public PointMissionRewardResponse checkAttendanceAndReward(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        if (registeredPhone.isTodayAttended()) {
            throw new MissionException(MissionExceptionType.TODAY_ALREADY_ATTENDED);
        }
        Point point = updatePointService.updatePoint(student, PointType.MISSION_VISIT_REWARD);
        int totalMissionPoint = getTotalMissionPoint(point);
        registeredPhone.updateLastAttendanceDateToToday();
        return PointMissionRewardResponse.from(point, PointType.MISSION_VISIT_REWARD.getPointChange(), totalMissionPoint);
    }

    public int getTotalMissionPoint(Point point) {
        return pointStatusRepository.findAllByPointOrderByCreatedDateDesc(point).stream()
                .filter(ps -> ps.getPointType().getChangeType() == PointChangeType.MISSION)
                .mapToInt(PointStatus::getChangedPoint)
                .sum();
    }
}
