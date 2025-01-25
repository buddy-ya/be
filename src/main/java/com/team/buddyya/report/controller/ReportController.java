package com.team.buddyya.report.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.report.dto.ReportRequest;
import com.team.buddyya.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createReport(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ReportRequest request) {
        reportService.createReport(userDetails.getStudentInfo(), request);
        return ResponseEntity.ok().build();
    }
}
