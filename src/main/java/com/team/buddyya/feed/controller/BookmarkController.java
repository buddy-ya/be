package com.team.buddyya.feed.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.response.BookmarkResponse;
import com.team.buddyya.feed.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds/{feedId}/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PutMapping
    public ResponseEntity<BookmarkResponse> toggleBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long feedId) {
        return ResponseEntity.ok(bookmarkService.toggleBookmark(userDetails.getStudentInfo(), feedId));
    }
}
