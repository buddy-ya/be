package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.dto.request.comment.CommentCreateRequest;
import com.team.buddyya.feed.dto.request.comment.CommentUpdateRequest;
import com.team.buddyya.feed.dto.response.comment.CommentCreateResponse;
import com.team.buddyya.feed.dto.response.comment.CommentListResponse;
import com.team.buddyya.feed.dto.response.comment.CommentResponse;
import com.team.buddyya.feed.dto.response.comment.CommentUpdateResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.CommentRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final FeedService feedService;
    private final StudentRepository studentRepository;

    public Comment findCommentByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.COMMENT_NOT_FOUND));
    }

    public CommentListResponse getComments(StudentInfo studentInfo, Long feedId) {
        Feed feed = feedService.findFeedByFeedId(feedId);
        List<CommentResponse> response = feed.getComments().stream()
                .map(CommentResponse::from)
                .toList();
        return CommentListResponse.from(response);
    }

    public CommentCreateResponse createComment(StudentInfo studentInfo, Long feedId, CommentCreateRequest request) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        Feed feed = feedService.findFeedByFeedId(feedId);
        Comment comment = Comment.builder()
                .student(student)
                .feed(feed)
                .content(request.content())
                .build();
        commentRepository.save(comment);
        return CommentCreateResponse.from(comment);
    }

    public CommentUpdateResponse updateComment(StudentInfo studentInfo, Long commentId, CommentUpdateRequest request) {
        Comment comment = findCommentByCommentId(commentId);
        validateCommentOwner(studentInfo.id(), comment);
        comment.updateComment(request.content());
        return CommentUpdateResponse.from(comment);
    }

    public void deleteComment(StudentInfo studentInfo, Long feedId, Long commentId) {
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
