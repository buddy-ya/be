package com.team.buddyya.certification.service;

import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MessageSendService {

    private static final String SOLAPI_API_URL = "https://api.solapi.com";

    @Value("${solapi.api.key}")
    private String apiKey;

    @Value("${solapi.api.secret}")
    private String apiSecret;

    @Value("${solapi.api.number}")
    private String fromPhoneNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    private void initMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, SOLAPI_API_URL);
    }

    public String sendMessage(String to) {
        String generatedCode = generateRandomNumber();
        Message message = new Message();
        message.setFrom(fromPhoneNumber);
        message.setTo(to);
        message.setText("[버디야] 본인 확인 인증번호[" + generatedCode + "]를 화면에 입력해주세요");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        if (!"2000".equals(response.getStatusCode())) {
            throw new PhoneAuthenticationException(ErrorCode.SMS_SEND_FAILED);
        }
        return generatedCode;
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(1_000_000));
    }
}
