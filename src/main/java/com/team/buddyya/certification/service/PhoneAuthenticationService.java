package com.team.buddyya.certification.service;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.dto.response.VerifyCodeResponse;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.student.repository.AvatarRepository;
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
    private final AvatarRepository avatarRepository;

    public SendCodeResponse saveCode(String phoneNumber, String generatedCode) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> new RegisteredPhone(phoneNumber, generatedCode));
        if (registeredPhone.getId() != null) {
            registeredPhone.updateAuthenticationCode(generatedCode);
        }
        registeredPhoneRepository.save(registeredPhone);
        return new SendCodeResponse(phoneNumber);
    }

    public void verifyCode(String phoneNumber, String inputCode) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.CODE_MISMATCH));
        if (!inputCode.equals(registeredPhone.getAuthenticationCode())) {
            throw new PhoneAuthenticationException(PhoneAuthenticationExceptionType.CODE_MISMATCH);
        }
    }

    public VerifyCodeResponse checkMembership(String phoneNumber) {
        boolean isExistingMember = avatarRepository.findByPhoneNumber(phoneNumber).isPresent();
        String status = isExistingMember ? EXISTING_MEMBER : NEW_MEMBER;
        return new VerifyCodeResponse(phoneNumber, status);
    }
}
