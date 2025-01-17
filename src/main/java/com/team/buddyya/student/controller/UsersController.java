package com.team.buddyya.student.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.response.feed.FeedListResponse;
import com.team.buddyya.feed.service.FeedService;
import com.team.buddyya.student.dto.request.MyPageUpdateInterestsRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateLanguagesRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateNameRequest;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.MyPageResponse;
import com.team.buddyya.student.dto.response.MyPageUpdateResponse;
import com.team.buddyya.student.dto.response.OnBoardingResponse;
import com.team.buddyya.student.dto.response.UserPageResponse;
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
public class UsersController {

    private final OnBoardingService onBoardingService;
    private final StudentService studentService;
    private final MyPageService myPageService;
    private final FeedService feedService;

    @PostMapping("/signup")
    public ResponseEntity<OnBoardingResponse> onboard(@RequestBody OnBoardingRequest request) {
        OnBoardingResponse response = onBoardingService.onboard(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/interests")
    public ResponseEntity<MyPageUpdateResponse> updateInterests(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @RequestBody MyPageUpdateInterestsRequest request) {
        return ResponseEntity.ok(myPageService.updateInterests(userDetails.getStudentInfo(), request));
    }

    @PatchMapping("/update/languages")
    public ResponseEntity<MyPageUpdateResponse> updateLanguages(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @RequestBody MyPageUpdateLanguagesRequest request) {
        return ResponseEntity.ok(myPageService.updateLanguages(userDetails.getStudentInfo(), request));
    }

    @PatchMapping("/update/name")
    public ResponseEntity<MyPageUpdateResponse> updateName(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestBody MyPageUpdateNameRequest request) {
        return ResponseEntity.ok(myPageService.updateName(userDetails.getStudentInfo(), request));
    }

    @PatchMapping("/update/profile-default-image")
    public ResponseEntity<MyPageUpdateResponse> updateProfileDefaultImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String profileImageKey) {
        return ResponseEntity.ok(
                myPageService.updateProfileDefaultImage(userDetails.getStudentInfo(), profileImageKey));
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponse> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(myPageService.getMyPage(userDetails.getStudentInfo()));
    }

    @GetMapping("/userpage/{studentId}")
    public ResponseEntity<UserPageResponse> getUserPage(@PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(myPageService.getUserPage(studentId));
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

    @DeleteMapping("/delete")
    public void deleteStudent(@AuthenticationPrincipal CustomUserDetails userDetails) {
        studentService.deleteStudent(userDetails.getStudentInfo());
    }
}