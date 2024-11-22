package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.Like;
import com.team.buddyya.feed.dto.response.LikeResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.LikeRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeSevice {

    private final LikeRepository likeRepository;
    private final StudentRepository studentRepository;
    private final FeedService feedService;

    public boolean existsByStudentIdAndFeedId(Long studentId, Long feedId) {
        return likeRepository.existsByStudentIdAndFeedId(studentId, feedId);
    }

    public Like findLikeByStudentIdAndFeedId(Long studentId, Long feedId) {
        return likeRepository.findByStudentIdAndFeedId(studentId, feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_LIKED));
    }

    public LikeResponse toggleLike(StudentInfo studentInfo, Long feedId) {
        Feed feed = feedService.findFeedById(feedId);
        boolean isLiked = existsByStudentIdAndFeedId(studentInfo.id(), feedId);
        if (isLiked) {
            Like like = findLikeByStudentIdAndFeedId(studentInfo.id(), feedId);
            likeRepository.delete(like);
        } else {
            Student student = studentRepository.findById(studentInfo.id())
                    .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
            Like like = Like.builder()
                    .feed(feed)
                    .student(student)
                    .build();
            likeRepository.save(like);
        }
        return LikeResponse.from(!isLiked, feed.getLikeCount());
    }


}
