package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByStudentIdAndFeedId(Long studentId, Long feedId);

    boolean existsByStudentIdAndFeedId(Long studentId, Long feedId);
}
