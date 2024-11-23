package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.BookMark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<BookMark, Long> {

    boolean existsByStudentIdAndFeedId(Long studentId, Long feedId);
}
