package com.team.buddyya.certification.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.request.SendStudentIdCardRequest;
import com.team.buddyya.certification.dto.response.CertificationResponse;
import com.team.buddyya.certification.dto.response.CertificationStatusResponse;
import com.team.buddyya.certification.dto.response.StudentIdCardResponse;
import com.team.buddyya.certification.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping("/email/send")
    public ResponseEntity<CertificationResponse> sendEmail(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody EmailCertificationRequest emailCertificationRequest) {
        return ResponseEntity.ok(certificationService.certificateEmail(userDetails.getStudentInfo(), emailCertificationRequest));
    }

    @PostMapping("/email/verify-code")
    public ResponseEntity<CertificationResponse> emailCodeCertificate(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody EmailCodeRequest codeRequest) {
        return ResponseEntity.ok(certificationService.certificateEmailCode(userDetails.getStudentInfo(), codeRequest));
    }

    @PostMapping("/student-id-card")
    public ResponseEntity<CertificationResponse> sendStudentIdCard(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute SendStudentIdCardRequest sendStudentIdCardRequest) {
        return ResponseEntity.ok(certificationService.uploadStudentIdCard(userDetails.getStudentInfo(), sendStudentIdCardRequest));
    }

    @GetMapping("/student-id-card")
    public ResponseEntity<StudentIdCardResponse> getStudentIdCard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(certificationService.getStudentIdCard(userDetails.getStudentInfo()));
    }

    @GetMapping
    public ResponseEntity<CertificationStatusResponse> checkCertificationStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(certificationService.isCertificated(userDetails.getStudentInfo()));
    }
}
