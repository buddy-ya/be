package com.team.buddyya.admin.controller;

import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.AdminReportResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardListResponse;
import com.team.buddyya.admin.dto.response.StudentVerificationResponse;
import com.team.buddyya.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/student-id-cards")
    public ResponseEntity<StudentIdCardListResponse> getStudentIdCards() {
        return ResponseEntity.ok(adminService.getStudentIdCards());
    }

    @PostMapping("/student-id-cards/verify")
    public ResponseEntity<StudentVerificationResponse> verifyStudentIdCard(@RequestBody StudentVerificationRequest request) {
        return ResponseEntity.ok(adminService.verifyStudentIdCard(request));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<AdminReportResponse>> getReports() {
        return ResponseEntity.ok(adminService.getAllReports());
    }
}
