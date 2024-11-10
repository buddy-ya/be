package com.team.buddyya.certification.service;

import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationErrorCode;
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
    private static final String MESSAGE_TEXT_FORMAT = "[버디야] 본인 확인 인증번호[%s]를 화면에 입력해주세요";
    private static final String MESSAGE_SEND_SUCCESS_STATUS_CODE = "2000";
    private static final int AUTH_CODE_MAX_RANGE = 1_000_000;

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
        Message message = createMessage(to, generatedCode);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        if (!response.getStatusCode().equals(MESSAGE_SEND_SUCCESS_STATUS_CODE)) {
            throw new PhoneAuthenticationException(PhoneAuthenticationErrorCode.SMS_SEND_FAILED);
        }
        return generatedCode;
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(AUTH_CODE_MAX_RANGE));
    }

    private Message createMessage(String to, String generatedCode) {
        Message message = new Message();
        message.setFrom(fromPhoneNumber);
        message.setTo(to);
        message.setText(String.format(MESSAGE_TEXT_FORMAT, generatedCode));
        return message;
    }
}
