package com.team.buddyya.certification.controller;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.dto.request.AdminAccountRequest;
import com.team.buddyya.certification.dto.request.SendCodeRequest;
import com.team.buddyya.certification.dto.request.TestAccountRequest;
import com.team.buddyya.certification.dto.request.VerifyCodeRequest;
import com.team.buddyya.certification.dto.response.AdminAccountResponse;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
import com.team.buddyya.certification.dto.response.TestAccountResponse;
import com.team.buddyya.certification.service.MessageSendService;
import com.team.buddyya.certification.service.PhoneAuthenticationService;
import com.team.buddyya.student.dto.response.UserResponse;
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
    private final MessageSendService messageSendService;

    @PostMapping("/send-code")
    public ResponseEntity<SendCodeResponse> sendOne(@RequestBody SendCodeRequest sendCodeRequest) {
        String generatedCode = messageSendService.sendMessage(sendCodeRequest.phoneNumber());
        SendCodeResponse response = phoneAuthenticationService.saveCode(sendCodeRequest.phoneNumber(), generatedCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<UserResponse> verifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest) {
        phoneAuthenticationService.verifyCode(verifyCodeRequest.phoneNumber(), verifyCodeRequest.code());
        UserResponse response = phoneAuthenticationService.checkMembership(verifyCodeRequest.phoneNumber());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-test-account")
    public ResponseEntity<TestAccountResponse> checkTestAccount(@RequestBody TestAccountRequest request) {
        TestAccountResponse response = phoneAuthenticationService.isTestAccount(request.phoneNumber());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-admin-account")
    public ResponseEntity<AdminAccountResponse> checkAdminAccount(@RequestBody AdminAccountRequest request) {
        AdminAccountResponse response = phoneAuthenticationService.isAdminAccount(request.phoneNumber());
        return ResponseEntity.ok(response);
    }
}
