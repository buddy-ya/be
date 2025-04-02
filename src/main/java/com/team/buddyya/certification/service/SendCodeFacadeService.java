package com.team.buddyya.certification.service;

import com.team.buddyya.certification.dto.request.SendCodeRequest;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SendCodeFacadeService {

    private final MessageSendService messageSendService;
    private final PhoneAuthenticationService phoneAuthenticationService;

    public SendCodeResponse sendCodeAndSave(SendCodeRequest request) {
        String generatedCode = messageSendService.sendMessage(request);
        return phoneAuthenticationService.saveCode(request.phoneNumber(), generatedCode);
    }
}