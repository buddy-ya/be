package com.team.buddyya.certification.controller;

import com.team.buddyya.certification.dto.request.SavePhoneInfoRequest;
import com.team.buddyya.certification.dto.request.SendCodeRequest;
import com.team.buddyya.certification.dto.request.VerifyCodeRequest;
import com.team.buddyya.certification.dto.response.SendCodeResponse;
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

    @PostMapping("/save-phone-info")
    public ResponseEntity<Void> savePhoneInfo(@RequestBody SavePhoneInfoRequest request){
        phoneAuthenticationService.savePhoneInfo(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-code")
    public ResponseEntity<SendCodeResponse> sendOne(@RequestBody SendCodeRequest request) {
        String generatedCode = messageSendService.sendMessage(request);
        SendCodeResponse response = phoneAuthenticationService.saveCode(request.phoneNumber(), generatedCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<UserResponse> verifyCode(@RequestBody VerifyCodeRequest request) {
        phoneAuthenticationService.verifyCode(request.phoneNumber(), request.code(), request.phoneInfo());
        UserResponse response = phoneAuthenticationService.checkMembership(request.phoneNumber());
        return ResponseEntity.ok(response);
    }
}
