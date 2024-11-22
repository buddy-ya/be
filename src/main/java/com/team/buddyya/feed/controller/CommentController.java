package com.team.buddyya.feed.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.request.CommentCreateRequest;
import com.team.buddyya.feed.dto.request.CommentUpdateRequest;
import com.team.buddyya.feed.dto.response.CommentCreateResponse;
import com.team.buddyya.feed.dto.response.CommentUpdateResponse;
import com.team.buddyya.feed.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentCreateResponse> createComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long feedId,
                                                               @RequestBody CommentCreateRequest request) {
        CommentCreateResponse response = commentService.createComment(userDetails.getStudentInfo(), feedId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentUpdateResponse> updateComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long commentId,
                                                               @RequestBody CommentUpdateRequest request) {
        CommentUpdateResponse response = commentService.updateComment(userDetails.getStudentInfo(), commentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long feedId,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(userDetails.getStudentInfo(), feedId, commentId);
        return ResponseEntity.noContent().build();
    }
}
