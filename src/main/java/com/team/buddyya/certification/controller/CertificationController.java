package com.team.buddyya.certification.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.response.EmailCertificationResponse;
import com.team.buddyya.certification.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping("/email")
    public ResponseEntity<EmailCertificationResponse> sendEmail(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody EmailCertificationRequest emailCertificationRequest) {
        return ResponseEntity.ok(certificationService.certificateEmail(userDetails.getStudentInfo(), emailCertificationRequest));
    }

    @PostMapping("/email/code")
    public ResponseEntity<EmailCertificationResponse> emailCodeCertificate(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody EmailCodeRequest codeRequest) {
        return ResponseEntity.ok(certificationService.certificateEmailCode(userDetails.getStudentInfo(), codeRequest));
    }
}
