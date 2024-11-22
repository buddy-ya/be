package com.team.buddyya.feed.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.service.LikeSevice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds/{feedId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeSevice likeSevice;

    @PutMapping
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long feedId) {
        likeSevice.toggleLike(userDetails.getStudentInfo(), feedId);
        return ResponseEntity.ok().build();
    }
}
