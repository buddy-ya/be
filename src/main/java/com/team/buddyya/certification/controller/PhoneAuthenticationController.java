package com.team.buddyya.certification.controller;

import com.team.buddyya.certification.dto.request.SendCodeRequest;
import com.team.buddyya.certification.dto.request.VerifyCodeRequest;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import com.team.buddyya.certification.dto.response.VerifyCodeResponse;
import com.team.buddyya.certification.service.PhoneAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/phone-auth")
@RestController
public class PhoneAuthenticationController {

    private final PhoneAuthenticationService phoneAuthenticationService;

    @PostMapping("/send-code")
    public ResponseEntity<SendCodeResponse> sendOne(@RequestBody SendCodeRequest sendCodeRequest) {

        String generatedCode = phoneAuthenticationService.sendSms(sendCodeRequest.phoneNumber());
        SendCodeResponse response = phoneAuthenticationService.saveCode(sendCodeRequest.phoneNumber(), generatedCode);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerifyCodeResponse> verifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest) {

        phoneAuthenticationService.verifyCode(verifyCodeRequest.phoneNumber(), verifyCodeRequest.code());
        VerifyCodeResponse response = phoneAuthenticationService.checkMembership(verifyCodeRequest.phoneNumber());

        return ResponseEntity.ok(response);
    }
}
