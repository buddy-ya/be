package com.team.buddyya.feed.controller;

import com.team.buddyya.feed.dto.request.FeedListRequest;
import com.team.buddyya.feed.dto.response.FeedListResponse;
import com.team.buddyya.feed.dto.response.FeedResponse;
import com.team.buddyya.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedListResponse> getFeeds(@ModelAttribute FeedListRequest request) {
        return ResponseEntity.ok(feedService.getFeeds(request));
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponse> getFeed(@PathVariable Long feedId) {
        return ResponseEntity.ok(feedService.getFeed(feedId))
    }

}
