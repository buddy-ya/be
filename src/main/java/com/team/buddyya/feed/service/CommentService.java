package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.CommentInfo;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.dto.request.comment.CommentCreateRequest;
import com.team.buddyya.feed.dto.request.comment.CommentUpdateRequest;
import com.team.buddyya.feed.dto.response.comment.CommentCreateResponse;
import com.team.buddyya.feed.dto.response.comment.CommentResponse;
import com.team.buddyya.feed.dto.response.comment.CommentUpdateResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.CommentRepository;
import com.team.buddyya.feed.respository.FeedRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;
    private final FindStudentService findStudentService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    Feed findFeedByFeedId(Long feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    Comment findCommentByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.COMMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        List<CommentInfo> commentInfos = feed.getComments().stream()
                .map(comment -> CommentInfo.from(comment, feed.getStudent().getId(), studentInfo.id()))
                .toList();
        return commentInfos.stream()
                .map(CommentResponse::from)
                .toList();
    }

    public CommentCreateResponse createComment(StudentInfo studentInfo, Long feedId, CommentCreateRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Feed feed = findFeedByFeedId(feedId);
        Comment comment = Comment.builder()
                .student(student)
                .feed(feed)
                .content(request.content())
                .build();
        commentRepository.save(comment);
        notificationService.sendFeedNotification(feed, request.content());
        CommentInfo commentInfo = CommentInfo.from(comment, feed.getStudent().getId(), studentInfo.id());
        return CommentCreateResponse.from(commentInfo);
    }

    public CommentUpdateResponse updateComment(StudentInfo studentInfo, Long feedId, Long commentId,
                                               CommentUpdateRequest request) {
        Feed feed = findFeedByFeedId(feedId);
        Comment comment = findCommentByCommentId(commentId);
        validateCommentOwner(studentInfo.id(), comment);
        comment.updateComment(request.content());
        CommentInfo commentInfo = CommentInfo.from(comment, feed.getStudent().getId(), studentInfo.id());
        return CommentUpdateResponse.from(commentInfo);
    }

    public void deleteComment(StudentInfo studentInfo, Long feedId, Long commentId) {
        Feed feed = findFeedByFeedId(feedId);
        Comment comment = findCommentByCommentId(commentId);
        validateCommentOwner(studentInfo.id(), comment);
        commentRepository.delete(comment);
    }

    private void validateCommentOwner(Long studentId, Comment comment) {
        if (!studentId.equals(comment.getStudent().getId())) {
            throw new FeedException(FeedExceptionType.NOT_COMMENT_OWNER);
        }
    }
}
