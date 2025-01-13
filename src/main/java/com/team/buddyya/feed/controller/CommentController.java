package com.team.buddyya.feed.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.feed.dto.request.comment.CommentCreateRequest;
import com.team.buddyya.feed.dto.request.comment.CommentUpdateRequest;
import com.team.buddyya.feed.dto.response.LikeResponse;
import com.team.buddyya.feed.dto.response.comment.CommentCreateResponse;
import com.team.buddyya.feed.dto.response.comment.CommentResponse;
import com.team.buddyya.feed.dto.response.comment.CommentUpdateResponse;
import com.team.buddyya.feed.service.CommentLikeService;
import com.team.buddyya.feed.service.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long feedId) {
        List<CommentResponse> response = commentService.getComments(customUserDetails.getStudentInfo(), feedId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentCreateResponse> createComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long feedId,
                                                               @RequestBody CommentCreateRequest request) {
        CommentCreateResponse response = commentService.createComment(userDetails.getStudentInfo(), feedId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentUpdateResponse> updateComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long feedId,
                                                               @PathVariable Long commentId,
                                                               @RequestBody CommentUpdateRequest request) {
        CommentUpdateResponse response = commentService.updateComment(userDetails.getStudentInfo(), feedId, commentId,
                request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long feedId,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(userDetails.getStudentInfo(), feedId, commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}/like")
    public ResponseEntity<LikeResponse> toggleCommentLike(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable Long feedId,
                                                          @PathVariable Long commentId) {
        LikeResponse response = commentLikeService.toggleLike(userDetails.getStudentInfo(), commentId);
        return ResponseEntity.ok(response);
    }
}
