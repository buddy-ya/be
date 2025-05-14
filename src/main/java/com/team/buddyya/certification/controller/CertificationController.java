package com.team.buddyya.certification.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.request.SendStudentIdCardRequest;
import com.team.buddyya.certification.dto.response.CertificationResponse;
import com.team.buddyya.certification.dto.response.CertificationStatusResponse;
import com.team.buddyya.certification.dto.response.StudentIdCardResponse;
import com.team.buddyya.certification.service.CertificationService;
import com.team.buddyya.certification.service.EmailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class CertificationController {

    private final CertificationService certificationService;
    private final EmailSendService emailSendService;

    @PostMapping("/email/send")
    public ResponseEntity<Void> sendEmail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestBody EmailCertificationRequest emailCertificationRequest) {

        String generatedCode = emailSendService.sendEmail(userDetails.getStudentInfo(), emailCertificationRequest);
        certificationService.saveCode(emailCertificationRequest.email(), generatedCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/verify-code")
    public ResponseEntity<CertificationResponse> emailCodeCertificate(
            @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody EmailCodeRequest codeRequest) {
        CertificationResponse response = certificationService.certificateEmailCode(userDetails.getStudentInfo(), codeRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/student-id-card")
    public ResponseEntity<Void> sendStudentIdCard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute SendStudentIdCardRequest sendStudentIdCardRequest) {
        certificationService.uploadStudentIdCard(userDetails.getStudentInfo(), sendStudentIdCardRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student-id-card")
    public ResponseEntity<StudentIdCardResponse> getStudentIdCard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(certificationService.getStudentIdCard(userDetails.getStudentInfo()));
    }

    @GetMapping
    public ResponseEntity<CertificationStatusResponse> checkCertificationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(certificationService.isCertificated(userDetails.getStudentInfo()));
    }

    @PutMapping("/refresh")
    public void refreshCertification(@AuthenticationPrincipal CustomUserDetails userDetails) {
        certificationService.refreshStudentCertification(userDetails.getStudentInfo());
    }
}
