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

    public boolean existsByStudentAndFeed(Student student, Feed feed) {
        return bookmarkRepository.existsByStudentAndFeed(student, feed);
    }

    private Bookmark findByStudentAndFeed(Student student, Feed feed) {
        return bookmarkRepository.findByStudentAndFeed(student, feed)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_BOOKMARKED));
    }

    public BookmarkResponse toggleBookmark(StudentInfo studentInfo, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
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
