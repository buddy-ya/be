package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.CommentLike;
import com.team.buddyya.feed.dto.response.LikeResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.CommentLikeRepository;
import com.team.buddyya.feed.respository.CommentRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final FindStudentService findStudentService;

    @Transactional(readOnly = true)
    boolean existsByCommentAndStudent(Comment comment, Student student) {
        return commentLikeRepository.existsByCommentAndStudent(comment, student);
    }

    @Transactional(readOnly = true)
    CommentLike findLikeByCommentAndStudent(Comment comment, Student student) {
        return commentLikeRepository.findByCommentAndStudent(comment, student)
                .orElseThrow(() -> new FeedException(FeedExceptionType.COMMENT_NOT_LIKED));
    }

    public LikeResponse toggleLike(StudentInfo studentInfo, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.COMMENT_NOT_FOUND));
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isLiked = existsByCommentAndStudent(comment, student);
        if (isLiked) {
            CommentLike commentLike = findLikeByCommentAndStudent(comment, student);
            commentLikeRepository.delete(commentLike);
            return LikeResponse.from(false, comment.getLikeCount());
        }
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .student(student)
                .build();
        commentLikeRepository.save(commentLike);
        return LikeResponse.from(true, comment.getLikeCount());
    }
}
