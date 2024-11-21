package com.team.buddyya.admin.controller;

import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.StudentIdCardListResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
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
        List<StudentIdCardResponse> studentIdCards = adminService.getStudentIdCards();
        return ResponseEntity.ok(new StudentIdCardListResponse(studentIdCards));
    }

    @PostMapping("/student-id-cards/verify")
    public ResponseEntity<StudentVerificationResponse> verifyStudentIdCard(@RequestBody StudentVerificationRequest request) {
        return ResponseEntity.ok(adminService.verifyStudentIdCard(request));
    }
}
