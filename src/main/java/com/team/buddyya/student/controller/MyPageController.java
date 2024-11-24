package com.team.buddyya.student.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.student.dto.request.MyPageUpdateInterestsRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateLanguagesRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateNameRequest;
import com.team.buddyya.student.dto.response.MyPageUpdateResponse;
import com.team.buddyya.student.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @PatchMapping("/update/interests")
    public ResponseEntity<MyPageUpdateResponse> updateInterests(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MyPageUpdateInterestsRequest request) {
        return ResponseEntity.ok(myPageService.updateInterests(userDetails.getStudentInfo(), request));
    }

    @PatchMapping("/update/languages")
    public ResponseEntity<MyPageUpdateResponse> updateLanguages(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MyPageUpdateLanguagesRequest request) {
        return ResponseEntity.ok(myPageService.updateLanguages(userDetails.getStudentInfo(), request));
    }

    @PatchMapping("/update/name")
    public ResponseEntity<MyPageUpdateResponse> updateName(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MyPageUpdateNameRequest request) {
        return ResponseEntity.ok(myPageService.updateName(userDetails.getStudentInfo(), request));
    }
}
