package com.team.buddyya.admin.controller;

import com.team.buddyya.admin.dto.response.StudentIdCardListResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/student-id-cards")
    public ResponseEntity<StudentIdCardListResponse> getStudentIdCards() {
        List<StudentIdCardResponse> studentIdCards = adminService.getStudentIdCards();
        return ResponseEntity.ok(new StudentIdCardListResponse(adminService.getStudentIdCards()));
    }
}
