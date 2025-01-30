package com.team.buddyya.certification.service;

import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
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
    private final JwtUtils jwtUtils;

    public SendCodeResponse saveCode(String phoneNumber, String generatedCode) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> new RegisteredPhone(phoneNumber, generatedCode));
        if (registeredPhone.getId() != null) {
            registeredPhone.updateAuthenticationCode(generatedCode);
        }
        registeredPhoneRepository.save(registeredPhone);
        return new SendCodeResponse(phoneNumber);
    }

    @Transactional(readOnly = true)
    public void verifyCode(String phoneNumber, String inputCode) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        if (!inputCode.equals(registeredPhone.getAuthenticationCode())) {
            throw new PhoneAuthenticationException(PhoneAuthenticationExceptionType.CODE_MISMATCH);
        }
    }

    public UserResponse checkMembership(String phoneNumber) {
        Optional<Student> optionalStudent = studentRepository.findByPhoneNumber(phoneNumber);

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            String accessToken = jwtUtils.createAccessToken(new TokenInfoRequest(student.getId()));
            String refreshToken = jwtUtils.createRefreshToken(new TokenInfoRequest(student.getId()));
            boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student).isPresent();
            return UserResponse.from(student, isStudentIdCardRequested, "EXISTING_MEMBER", accessToken, refreshToken);
        }

        return UserResponse.from("NEW_MEMBER");
    }
}
