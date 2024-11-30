package com.team.buddyya.feed.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.request.feed.FeedCreateRequest;
import com.team.buddyya.feed.dto.request.feed.FeedListRequest;
import com.team.buddyya.feed.dto.request.feed.FeedUpdateRequest;
import com.team.buddyya.feed.dto.response.feed.FeedListResponse;
import com.team.buddyya.feed.dto.response.feed.FeedResponse;
import com.team.buddyya.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedListResponse> getFeeds(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @PageableDefault(size = 10, sort = "createdDate", direction = Direction.DESC) Pageable pageable,
                                                     @ModelAttribute FeedListRequest request) {
        FeedListResponse response = feedService.getFeeds(userDetails.getStudentInfo(), pageable, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<FeedListResponse> getPopularFeeds(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PageableDefault(size = 10, sort = "createdDate", direction = Direction.DESC) Pageable pageable) {
        FeedListResponse response = feedService.getPopularFeeds(userDetails.getStudentInfo(), pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute FeedCreateRequest request) {
        feedService.createFeed(userDetails.getStudentInfo(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponse> getFeed(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @PathVariable Long feedId) {
        FeedResponse response = feedService.getFeed(userDetails.getStudentInfo(), feedId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{feedId}")
    public ResponseEntity<Void> updateFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long feedId,
            @ModelAttribute FeedUpdateRequest request) {
        feedService.updateFeed(userDetails.getStudentInfo(), feedId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long feedId) {
        feedService.deleteFeed(userDetails.getStudentInfo(), feedId);
        return ResponseEntity.noContent().build();
    }
}
