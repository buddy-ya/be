package com.team.buddyya.certification.service;

import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.certification.domain.PhoneInfo;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.dto.response.AdminAccountResponse;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import com.team.buddyya.certification.dto.response.TestAccountResponse;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.AdminAccountRepository;
import com.team.buddyya.certification.repository.PhoneInfoRepository;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.certification.repository.TestAccountRepository;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.service.FindPointService;
import com.team.buddyya.student.domain.MatchingProfile;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.MatchingProfileRepository;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.service.InvitationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PhoneAuthenticationService {

    private static final String EXISTING_MEMBER = "EXISTING_MEMBER";
    private static final String NEW_MEMBER = "NEW_MEMBER";

    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final StudentRepository studentRepository;
    private final StudentIdCardRepository studentIdCardRepository;
    private final PhoneInfoRepository phoneInfoRepository;
    private final TestAccountRepository testAccountRepository;
    private final FindPointService findPointService;
    private final AdminAccountRepository adminAccountRepository;
    private final MatchingProfileRepository matchingProfileRepository;
    private final InvitationService invitationService;
    private final JwtUtils jwtUtils;

    public SendCodeResponse saveCode(String phoneNumber, String generatedCode) {
        RegisteredPhone registeredPhone = getOrCreatePhone(phoneNumber, generatedCode);
        registeredPhoneRepository.save(registeredPhone);
        return new SendCodeResponse(phoneNumber);
    }

    public void verifyCode(String phoneNumber, String inputCode, String udId) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        if (testAccountRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            resetMessageCountIfExists(udId);
            return;
        }
        if (!inputCode.equals(registeredPhone.getAuthenticationCode())) {
            throw new PhoneAuthenticationException(PhoneAuthenticationExceptionType.CODE_MISMATCH);
        }
        resetMessageCountIfExists(udId);
    }

    public UserResponse checkMembership(String phoneNumber) {
        Optional<Student> optionalStudent = studentRepository.findByPhoneNumber(phoneNumber);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            String accessToken = jwtUtils.createAccessToken(new TokenInfoRequest(student.getId()));
            String refreshToken = student.getAuthToken().getRefreshToken();
            student.getAvatar().setLoggedOut(false);
            boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student).isPresent();
            Point point = findPointService.findByStudent(student);
            MatchingProfile matchingProfile = getMatchingProfile(student);
            return UserResponse.fromCheckMembership(student, isStudentIdCardRequested, EXISTING_MEMBER, accessToken,
                    refreshToken, point, matchingProfile);
        }
        return UserResponse.fromCheckMembership(NEW_MEMBER);
    }

    private RegisteredPhone getOrCreatePhone(String phoneNumber, String generatedCode) {
        return registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .map(phone -> {
                    phone.updateAuthenticationCode(generatedCode);
                    return phone;
                })
                .orElse(RegisteredPhone.builder()
                        .phoneNumber(phoneNumber)
                        .authenticationCode(generatedCode)
                        .invitationCode(invitationService.createInvitationCode())
                        .build()
                );
    }

    public TestAccountResponse isTestAccount(String phoneNumber) {
        if (testAccountRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            return new TestAccountResponse(true);
        }
        return new TestAccountResponse(false);
    }

    public AdminAccountResponse isAdminAccount(String phoneNumber) {
        if (adminAccountRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            return new AdminAccountResponse(true);
        }
        return new AdminAccountResponse(false);
    }

    private void resetMessageCountIfExists(String udId) {
        if (udId == null) {
            return;
        }
        PhoneInfo phoneInfo = phoneInfoRepository.findPhoneInfoByUdId(udId)
                .orElseThrow(
                        () -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_INFO_NOT_FOUND));
        phoneInfo.resetMessageSendCount();
    }

    private MatchingProfile getMatchingProfile(Student student) {
        return matchingProfileRepository.findByStudent(student)
                .orElseThrow(() -> new StudentException(StudentExceptionType.MATCHING_PROFILE_NOT_FOUND));
    }
}
