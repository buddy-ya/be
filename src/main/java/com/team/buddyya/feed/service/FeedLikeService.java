package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedLike;
import com.team.buddyya.feed.dto.response.LikeResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.repository.FeedLikeRepository;
import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedLikeService {

    private final FeedLikeRepository feedLikeRepository;
    private final FindStudentService findStudentService;
    private final FeedRepository feedRepository;

    @Transactional(readOnly = true)
    boolean existsByStudentAndFeed(Student student, Feed feed) {
        return feedLikeRepository.existsByStudentAndFeed(student, feed);
    }

    @Transactional(readOnly = true)
    FeedLike findLikeByStudentAndFeed(Student student, Feed feed) {
        return feedLikeRepository.findByStudentAndFeed(student, feed)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_LIKED));
    }

    public LikeResponse toggleLike(StudentInfo studentInfo, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isLiked = existsByStudentAndFeed(student, feed);
        if (isLiked) {
            FeedLike feedLike = findLikeByStudentAndFeed(student, feed);
            feedLikeRepository.delete(feedLike);
            return LikeResponse.from(false, feed.getLikeCount());
        }
        FeedLike feedLike = FeedLike.builder()
                .feed(feed)
                .student(student)
                .build();
        feedLikeRepository.save(feedLike);
        return LikeResponse.from(true, feed.getLikeCount());
    }
}
