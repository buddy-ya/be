package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.service.UpdatePointService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.response.InvitationCodeResponse;
import com.team.buddyya.student.dto.response.ValidateInvitationCodeResponse;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitationService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final UpdatePointService updatePointService;
    private final FindStudentService findStudentService;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public String createInvitationCode() {
        return generateUniqueCode();
    }

    @Transactional(readOnly = true)
    public InvitationCodeResponse getInvitationCode(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        RegisteredPhone registeredPhone = findRegisteredPhone(student.getPhoneNumber());
        return InvitationCodeResponse.from(registeredPhone);
    }

    public ValidateInvitationCodeResponse validateInvitationCode(StudentInfo studentInfo, String code) {
        Student invitingStudent = validateAndFindInvitingStudent(studentInfo.id(), code);
        Student requestedStudent = findStudentService.findByStudentId(studentInfo.id());
        RegisteredPhone requestedPhone = findRegisteredPhone(requestedStudent.getPhoneNumber());
        validateNotAlreadyParticipated(requestedPhone);
        Point point = updatePointService.updatePoint(requestedStudent, PointType.INVITATION_EVENT);
        updatePointService.updatePoint(invitingStudent, PointType.INVITATION_EVENT);
        notificationService.sendInvitationRewardNotification(invitingStudent);
        requestedPhone.markAsInvitationEventParticipated();
        return ValidateInvitationCodeResponse.from(point, PointType.INVITATION_EVENT);
    }

    private Student validateAndFindInvitingStudent(Long requestedStudentId, String code) {
        RegisteredPhone invitingStudentPhone = registeredPhoneRepository.findByInvitationCode(code)
                .orElseThrow(() -> new StudentException(StudentExceptionType.INVALID_INVITATION_CODE));
        Student invitingStudent = studentRepository.findByPhoneNumber(invitingStudentPhone.getPhoneNumber())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        if (requestedStudentId.equals(invitingStudent.getId())) {
            throw new StudentException(StudentExceptionType.SELF_INVITATION_CODE);
        }
        return invitingStudent;
    }

    private void validateNotAlreadyParticipated(RegisteredPhone requestedPhone) {
        if (requestedPhone.getInvitationEventParticipated()) {
            throw new StudentException(StudentExceptionType.INVITATION_EVENT_ALREADY_PARTICIPATED);
        }
    }

    private RegisteredPhone findRegisteredPhone(String phoneNumber) {
        return registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (registeredPhoneRepository.findByInvitationCode(code).isPresent());
        return code;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
