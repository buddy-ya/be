package com.team.buddyya.student.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.response.feed.FeedListResponse;
import com.team.buddyya.feed.service.FeedService;
import com.team.buddyya.student.dto.request.ValidateInvitationCodeRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateRequest;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.request.UpdateProfileImageRequest;
import com.team.buddyya.student.dto.response.*;
import com.team.buddyya.student.service.InvitationService;
import com.team.buddyya.student.service.OnBoardingService;
import com.team.buddyya.student.service.StudentService;
import com.team.buddyya.student.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final OnBoardingService onBoardingService;
    private final StudentService studentService;
    private final FeedService feedService;
    private final UniversityService universityService;
    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<UserResponse> onboard(@RequestBody OnBoardingRequest request) {
        UserResponse response = onBoardingService.onboard(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(studentService.getUserInfo(userDetails.getStudentInfo(), userId));
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MyPageUpdateRequest request) {
        UserResponse response = studentService.updateUser(userDetails.getStudentInfo(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<UserResponse> updateProfileDefaultImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("isDefault") boolean isDefault,
            @ModelAttribute UpdateProfileImageRequest request) {
        return ResponseEntity.ok(
                studentService.updateUserProfileImage(userDetails.getStudentInfo(), isDefault, request));
    }

    @GetMapping("/myfeed")
    public ResponseEntity<FeedListResponse> getMyFeed(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(feedService.getMyFeed(userDetails.getStudentInfo(), pageable));
    }

    @GetMapping("/bookmark")
    public ResponseEntity<FeedListResponse> getBookmarkFeed(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(feedService.getBookmarkFeed(userDetails.getStudentInfo(), pageable));
    }

    @DeleteMapping
    public void deleteStudent(@AuthenticationPrincipal CustomUserDetails userDetails) {
        studentService.deleteStudent(userDetails.getStudentInfo());
    }

    @PostMapping("/block/{userId}")
    public ResponseEntity<BlockResponse> blockStudent(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @PathVariable("userId") Long userId) {
        BlockResponse response = studentService.blockStudent(userDetails.getStudentInfo().id(), userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        studentService.logout(userDetails.getStudentInfo());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/universities")
    public ResponseEntity<List<UniversityResponse>> getActiveUniversity() {
        return ResponseEntity.ok(universityService.getActiveUniversities());
    }

    @GetMapping("/invitation-code")
    public ResponseEntity<InvitationCodeResponse> getInvitationCode(@AuthenticationPrincipal CustomUserDetails userDetails) {
        InvitationCodeResponse response = invitationService.getInvitationCode(userDetails.getStudentInfo());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invitation-code")
    public ResponseEntity<ValidateInvitationCodeResponse> validateInvitationCode(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @RequestBody ValidateInvitationCodeRequest request){
        ValidateInvitationCodeResponse response = invitationService.validateInvitationCode(userDetails.getStudentInfo(), request.code());
        return ResponseEntity.ok(response);
    }
}