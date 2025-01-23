package com.team.buddyya.student.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.response.feed.FeedListResponse;
import com.team.buddyya.feed.service.FeedService;
import com.team.buddyya.student.dto.request.MyPageUpdateRequest;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.MyPageUpdateResponse;
import com.team.buddyya.student.dto.response.OnBoardingResponse;
import com.team.buddyya.student.dto.response.UserProfileResponse;
import com.team.buddyya.student.service.MyPageService;
import com.team.buddyya.student.service.OnBoardingService;
import com.team.buddyya.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final OnBoardingService onBoardingService;
    private final StudentService studentService;
    private final MyPageService myPageService;
    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<OnBoardingResponse> onboard(@RequestBody OnBoardingRequest request) {
        OnBoardingResponse response = onBoardingService.onboard(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(myPageService.getUserProfile(userDetails.getStudentInfo(), userId));
    }

    @PatchMapping
    public ResponseEntity<MyPageUpdateResponse> updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MyPageUpdateRequest request) {
        MyPageUpdateResponse response = myPageService.updateUser(userDetails.getStudentInfo(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/profile-default-image")
    public ResponseEntity<MyPageUpdateResponse> updateProfileDefaultImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String profileImageKey) {
        return ResponseEntity.ok(
                myPageService.updateProfileDefaultImage(userDetails.getStudentInfo(), profileImageKey));
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
}