package com.team.buddyya.feed.service;

import com.team.buddyya.feed.respository.BookmarkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public boolean isBookmarkedByStudent(Long studentId, Long feedId) {
        return bookmarkRepository.existsByStudentIdAndFeedId(studentId, feedId);
    }
}
