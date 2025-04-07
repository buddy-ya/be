package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.dto.request.comment.CommentCreateRequest;
import com.team.buddyya.feed.dto.request.comment.CommentUpdateRequest;
import com.team.buddyya.feed.dto.response.comment.CommentResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.repository.CommentLikeRepository;
import com.team.buddyya.feed.repository.CommentRepository;
import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.repository.BlockRepository;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.team.buddyya.student.domain.Role.ADMIN;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final FindStudentService findStudentService;
    private final NotificationService notificationService;
    private final BlockRepository blockRepository;

    @Transactional(readOnly = true)
    protected Feed findFeedByFeedId(Long feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    protected Comment findCommentByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.COMMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        Set<Long> blockedStudentIds = blockRepository.findBlockedStudentIdByBlockerId(studentInfo.id());
        List<Comment> comments = feed.getComments().stream()
                .filter(comment -> comment.getParent() == null)
                .filter(comment -> {
                    boolean isBlocked = blockedStudentIds.contains(comment.getStudent().getId());
                    boolean hasChildren = !comment.getChildren().isEmpty();
                    return hasChildren || !isBlocked;
                })
                .toList();
        return comments.stream()
                .map(comment -> CommentResponse.from(comment, feedId, studentInfo.id(), commentLikeRepository, blockedStudentIds))
                .toList();
    }

    public void createComment(StudentInfo studentInfo, Long feedId, CommentCreateRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Feed feed = findFeedByFeedId(feedId);
        Comment parent = null;
        if (request.parentId() != null) {
            parent = findCommentByCommentId(request.parentId());
            if (parent.getParent() != null) {
                throw new FeedException(FeedExceptionType.COMMENT_DEPTH_LIMIT);
            }
            notificationService.sendCommentReplyNotification(studentInfo.id(), feed, parent, request.content());
        }
        Comment comment = Comment.builder()
                .student(student)
                .feed(feed)
                .content(request.content())
                .parent(parent)
                .build();
        commentRepository.save(comment);
        notificationService.sendCommentNotification(studentInfo.id(), feed, request.content());
    }

    public void updateComment(StudentInfo studentInfo, Long feedId, Long commentId,
                              CommentUpdateRequest request) {
        Feed feed = findFeedByFeedId(feedId);
        Comment comment = findCommentByCommentId(commentId);
        if (comment.isDeleted()) {
            throw new FeedException(FeedExceptionType.COMMENT_NOT_FOUND);
        }
        validateCommentOwner(studentInfo, comment);
        comment.updateComment(request.content());
    }

    public void deleteComment(StudentInfo studentInfo, Long feedId, Long commentId) {
        Feed feed = findFeedByFeedId(feedId);
        Comment comment = findCommentByCommentId(commentId);
        if (comment.isDeleted()) {
            throw new FeedException(FeedExceptionType.COMMENT_NOT_FOUND);
        }
        validateCommentOwner(studentInfo, comment);
        boolean hasChild = !comment.getChildren().isEmpty();
        if (hasChild) {
            comment.updateIsDeleted(true);
            commentRepository.save(comment);
            return;
        }
        commentRepository.delete(comment);
    }

    private void validateCommentOwner(StudentInfo studentInfo, Comment comment) {
        if (!studentInfo.id().equals(comment.getStudent().getId()) && !studentInfo.role().equals(ADMIN)) {
            throw new FeedException(FeedExceptionType.NOT_COMMENT_OWNER);
        }
    }
}
