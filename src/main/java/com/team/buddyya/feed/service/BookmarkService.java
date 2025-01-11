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
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StudentRepository studentRepository;
    private final FeedRepository feedRepository;

    public boolean existsByStudentIdAndFeedId(Long studentId, Long feedId) {
        return bookmarkRepository.existsByStudentIdAndFeedId(studentId, feedId);
    }

    private Bookmark findByStudentIdAndFeedId(Long studentId, Long feedId) {
        return bookmarkRepository.findByStudentIdAndFeedId(studentId, feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_BOOKMARKED));
    }

    public BookmarkResponse toggleBookmark(StudentInfo studentInfo, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
        boolean isBookmarked = existsByStudentIdAndFeedId(studentInfo.id(), feedId);
        if (isBookmarked) {
            Bookmark bookmark = findByStudentIdAndFeedId(studentInfo.id(), feedId);
            bookmarkRepository.delete(bookmark);
            return BookmarkResponse.from(false);
        }
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        Bookmark bookmark = Bookmark.builder()
                .feed(feed)
                .student(student)
                .build();
        bookmarkRepository.save(bookmark);
        return BookmarkResponse.from(true);
    }
}
