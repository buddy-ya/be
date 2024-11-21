package com.team.buddyya.feed.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.request.FeedCreateRequest;
import com.team.buddyya.feed.dto.request.FeedListRequest;
import com.team.buddyya.feed.dto.request.FeedUpdateRequest;
import com.team.buddyya.feed.dto.response.FeedListResponse;
import com.team.buddyya.feed.dto.response.FeedResponse;
import com.team.buddyya.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedListResponse> getFeeds(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @ModelAttribute FeedListRequest request) {
        FeedListResponse response = feedService.getFeeds(userDetails.getStudentInfo(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createFeed(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestBody FeedCreateRequest request) {
        feedService.createFeed(userDetails.getStudentInfo(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponse> getFeed(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @PathVariable Long feedId) {
        FeedResponse response = feedService.getFeed(userDetails.getStudentInfo(), feedId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{feedId}")
    public ResponseEntity<Void> updateFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long feedId,
            @RequestBody FeedUpdateRequest request) {
        feedService.updateFeed(userDetails.getStudentInfo(), feedId, request);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long feedId) {
        feedService.deleteFeed(userDetails.getStudentInfo(), feedId);
        return ResponseEntity.noContent().build();
    }
}
