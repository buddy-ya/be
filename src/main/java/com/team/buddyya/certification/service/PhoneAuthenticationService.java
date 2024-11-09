package com.team.buddyya.certification.service;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.dto.response.VerifyCodeResponse;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import com.team.buddyya.common.exception.ErrorCode;
import com.team.buddyya.student.repository.AvatarRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PhoneAuthenticationService {
    private static final String EXISTING_MEMBER = "EXISTING_MEMBER";
    private static final String NEW_MEMBER = "NEW_MEMBER";
    private static final String SOLAPI_API_URL = "https://api.solapi.com";

    @Value("${solapi.api.key}")
    private String apiKey;

    @Value("${solapi.api.secret}")
    private String apiSecret;

    @Value("${solapi.api.number}")
    private String fromPhoneNumber;

    private DefaultMessageService messageService;
    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final AvatarRepository avatarRepository;

    @PostConstruct
    private void initMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, SOLAPI_API_URL);
    }

    public String sendSms(String to) {
        String numStr = generateRandomNumber();
        Message message = new Message();
        message.setFrom(fromPhoneNumber);
        message.setTo(to);
        message.setText("[버디야] 본인 확인 인증번호[" + numStr + "]를 화면에 입력해주세요");
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        String statusCode = response.getStatusCode();
        if (!statusCode.equals("2000")) {
            throw new PhoneAuthenticationException(ErrorCode.SMS_SEND_FAILED);
        }
        return numStr;
    }

    public SendCodeResponse saveCode(String phoneNumber, String generatedCode) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> new RegisteredPhone(phoneNumber, generatedCode));
        if (registeredPhone.getId() != null) {
            registeredPhone.updateAuthenticationCode(generatedCode);
        }
        registeredPhoneRepository.save(registeredPhone);
        return new SendCodeResponse(phoneNumber);
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(1_000_000));
    }

    public void verifyCode(String phoneNumber, String inputCode) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(ErrorCode.CODE_MISMATCH));
        if (!inputCode.equals(registeredPhone.getAuthenticationCode())) {
            throw new PhoneAuthenticationException(ErrorCode.CODE_MISMATCH);
        }
    }

    public VerifyCodeResponse checkMembership(String phoneNumber) {
        boolean isExistingMember = avatarRepository.findByPhoneNumber(phoneNumber).isPresent();
        String status = isExistingMember ? EXISTING_MEMBER : NEW_MEMBER;
        return new VerifyCodeResponse(phoneNumber, status);
    }
}
