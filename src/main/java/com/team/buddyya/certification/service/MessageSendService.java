package com.team.buddyya.certification.service;

import com.team.buddyya.certification.domain.PhoneInfo;
import com.team.buddyya.certification.dto.request.SendCodeRequest;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.TestAccountRepository;
import com.team.buddyya.certification.repository.PhoneInfoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageSendService {

    private static final String SOLAPI_API_URL = "https://api.solapi.com";
    private static final String MESSAGE_TEXT_FORMAT = "[Buddyya] Enter the code [%s] on the screen to confirm it's you!";
    private static final String MESSAGE_SEND_SUCCESS_STATUS_CODE = "2000";
    private static final int AUTH_CODE_MAX_RANGE = 1_000_000;

    private final TestAccountRepository testAccountRepository;

    @Value("${solapi.api.key}")
    private String apiKey;

    @Value("${solapi.api.secret}")
    private String apiSecret;

    @Value("${solapi.api.number}")
    private String fromPhoneNumber;

    private DefaultMessageService messageService;
    private final PhoneInfoRepository phoneInfoRepository;

    @PostConstruct
    private void initMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, SOLAPI_API_URL);
    }

    public String sendMessage(SendCodeRequest request) {
        if (request.udId() != null) {
            validateMessageCount(request.udId());
        }
        String generatedCode = generateRandomNumber();
        if (testAccountRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            return generatedCode;
        }
        Message message = createMessage(request.phoneNumber(), generatedCode);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        if (!response.getStatusCode().equals(MESSAGE_SEND_SUCCESS_STATUS_CODE)) {
            throw new PhoneAuthenticationException(PhoneAuthenticationExceptionType.SMS_SEND_FAILED);
        }
        return generatedCode;
    }

    private void validateMessageCount(String udId){
        PhoneInfo phoneInfo = findOrCreatePhoneInfo(udId);
        if (phoneInfo.isMaxSendMessageCount()) {
            throw new PhoneAuthenticationException(PhoneAuthenticationExceptionType.MAX_SMS_SEND_COUNT);
        }
        phoneInfo.increaseMessageSendCount();
    }

    private PhoneInfo findOrCreatePhoneInfo(String udId) {
        return phoneInfoRepository.findPhoneInfoByUdId(udId)
                .orElseGet(() -> phoneInfoRepository.save(
                        PhoneInfo.builder()
                                .udId(udId)
                                .build()));
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
