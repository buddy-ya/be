package com.team.buddyya.student.service;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.student.domain.InvitationCode;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.repository.InvitationCodeRepository;
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

    private final InvitationCodeRepository invitationCodeRepository;
    private final RegisteredPhoneRepository registeredPhoneRepository;

    public void createInvitationCode(Student student) {
        RegisteredPhone registeredPhone = findRegisteredPhone(student.getPhoneNumber());
        String uniqueCode = generateUniqueCode();
        InvitationCode invitationCode = InvitationCode.builder()
                .code(uniqueCode)
                .student(student)
                .registeredPhone(registeredPhone)
                .build();
        invitationCodeRepository.save(invitationCode);
    }

    private RegisteredPhone findRegisteredPhone(String phoneNumber) {
        return registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (invitationCodeRepository.findByCode(code).isPresent());
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
