package com.team.buddyya.certification.service;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.dto.response.VerifyCodeResponse;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import com.team.buddyya.common.exception.ErrorCode;
import com.team.buddyya.student.repository.StudentRepository;
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

    @Value("${solapi.api.key}")
    private String apiKey;

    @Value("${solapi.api.secret}")
    private String apiSecret;

    @Value("${solapi.api.number}")
    private String fromPhoneNumber;

    private DefaultMessageService messageService;
    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final StudentRepository studentRepository;

    @PostConstruct
    private void initMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
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
            registeredPhone.setAuthenticationCode(generatedCode);
        }

        registeredPhoneRepository.save(registeredPhone);

        return new SendCodeResponse(phoneNumber);
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            numStr.append(rand.nextInt(10));
        }
        return numStr.toString();
    }

    public void verifyCode(String phoneNumber, String inputCode) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(ErrorCode.CODE_MISMATCH));

        if (!inputCode.equals(registeredPhone.getAuthenticationCode())) {
            throw new PhoneAuthenticationException(ErrorCode.CODE_MISMATCH);
        }
    }
}
