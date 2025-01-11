package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.Like;
import com.team.buddyya.feed.dto.response.LikeResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.FeedRepository;
import com.team.buddyya.feed.respository.LikeRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final StudentRepository studentRepository;
    private final FeedRepository feedRepository;

    @Transactional(readOnly = true)
    boolean existsByStudentAndFeed(Student student, Feed feed) {
        return likeRepository.existsByStudentAndFeed(student, feed);
    }

    @Transactional(readOnly = true)
    Like findLikeByStudentAndFeed(Student student, Feed feed) {
        return likeRepository.findByStudentAndFeed(student, feed)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_LIKED));
    }

    public LikeResponse toggleLike(StudentInfo studentInfo, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        boolean isLiked = existsByStudentAndFeed(student, feed);
        if (isLiked) {
            Like like = findLikeByStudentAndFeed(student, feed);
            likeRepository.delete(like);
            return LikeResponse.from(false, feed.getLikeCount());
        }
        Like like = Like.builder()
                .feed(feed)
                .student(student)
                .build();
        likeRepository.save(like);
        return LikeResponse.from(true, feed.getLikeCount());
    }
}
