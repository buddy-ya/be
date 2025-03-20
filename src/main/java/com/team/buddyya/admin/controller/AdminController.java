package com.team.buddyya.admin.controller;

import com.team.buddyya.admin.dto.request.BanRequest;
import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.AdminChatMessageResponse;
import com.team.buddyya.admin.dto.response.AdminReportResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.admin.dto.response.StudentVerificationResponse;
import com.team.buddyya.admin.service.AdminService;
import com.team.buddyya.report.domain.ReportType;
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
    public ResponseEntity<List<StudentIdCardResponse>> getStudentIdCards() {
        List<StudentIdCardResponse> response = adminService.getStudentIdCards();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/student-id-cards/verify")
    public ResponseEntity<StudentVerificationResponse> verifyStudentIdCard(@RequestBody StudentVerificationRequest request) {
        StudentVerificationResponse response = adminService.verifyStudentIdCard(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/{type}")
    public ResponseEntity<List<AdminReportResponse>> getReportsByType(@PathVariable("type") ReportType type) {
        List<AdminReportResponse> response = adminService.getReportsByType(type);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable("reportId") Long reportId) {
        adminService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/ban/{studentId}")
    public ResponseEntity<Void> banStudent(
            @PathVariable("studentId") Long studentId,
            @RequestBody BanRequest request) {
        adminService.banStudent(studentId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unban/{studentId}")
    public ResponseEntity<Void> unbanStudent(@PathVariable("studentId") Long studentId) {
        adminService.unbanStudent(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chatroom/{roomId}/chats")
    public ResponseEntity<List<AdminChatMessageResponse>> getAllChatMessages(@PathVariable("roomId") Long roomId) {
        List<AdminChatMessageResponse> response = adminService.getAllChatMessages(roomId);
        return ResponseEntity.ok(response);
    }
}
