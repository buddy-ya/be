package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Bookmark;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.dto.response.BookmarkResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.BookmarkRepository;
import com.team.buddyya.feed.respository.FeedRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FindStudentService findStudentService;
    private final FeedRepository feedRepository;

    @Transactional(readOnly = true)
    boolean existsByStudentAndFeed(Student student, Feed feed) {
        return bookmarkRepository.existsByStudentAndFeed(student, feed);
    }

    @Transactional(readOnly = true)
    Bookmark findByStudentAndFeed(Student student, Feed feed) {
        return bookmarkRepository.findByStudentAndFeed(student, feed)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_BOOKMARKED));
    }

    public BookmarkResponse toggleBookmark(StudentInfo studentInfo, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isBookmarked = existsByStudentAndFeed(student, feed);
        if (isBookmarked) {
            Bookmark bookmark = findByStudentAndFeed(student, feed);
            bookmarkRepository.delete(bookmark);
            return BookmarkResponse.from(false);
        }
        Bookmark bookmark = Bookmark.builder()
                .feed(feed)
                .student(student)
                .build();
        bookmarkRepository.save(bookmark);
        return BookmarkResponse.from(true);
    }
}
