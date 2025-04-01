package com.team.buddyya.certification.service;

import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.certification.domain.PhoneInfo;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.dto.response.AdminAccountResponse;
import com.team.buddyya.certification.dto.request.SavePhoneInfoRequest;
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
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    private final InvitationService invitationService;
    private final JwtUtils jwtUtils;

    @Value("${test.phone.number.prefix}")
    private String testPhoneNumberPrefix;

    public SendCodeResponse saveCode(String phoneNumber, String generatedCode) {
        RegisteredPhone registeredPhone = getOrCreatePhone(phoneNumber, generatedCode);
        registeredPhoneRepository.save(registeredPhone);
        return new SendCodeResponse(phoneNumber);
    }

    @Transactional(readOnly = true)
    public void verifyCode(String phoneNumber, String inputCode, String udId) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        if (testAccountRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            return;
        }
        if (!inputCode.equals(registeredPhone.getAuthenticationCode())) {
            throw new PhoneAuthenticationException(PhoneAuthenticationExceptionType.CODE_MISMATCH);
        }
        PhoneInfo phoneInfo = phoneInfoRepository.findPhoneInfoByUdId(udId)
                .orElseThrow(()-> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_INFO_NOT_FOUND));
        phoneInfo.resetMessageSendCount();
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
            return UserResponse.fromCheckMembership(student, isStudentIdCardRequested, EXISTING_MEMBER, accessToken, refreshToken, point);
        }
        return UserResponse.fromCheckMembership(NEW_MEMBER);
    }

    private RegisteredPhone getOrCreatePhone(String phoneNumber, String generatedCode) {
        return registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .map(phone -> {
                    phone.updateAuthenticationCode(generatedCode);
                    return phone;
                })
                .orElse(new RegisteredPhone(phoneNumber, generatedCode, invitationService.createInvitationCode()));
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
}
