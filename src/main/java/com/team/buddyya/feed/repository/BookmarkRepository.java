package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Bookmark;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.student.domain.Student;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByStudentAndFeed(Student student, Feed feed);

    boolean existsByStudentAndFeed(Student student, Feed feed);

    Page<Bookmark> findAllByStudent(Student student, Pageable pageable);

    @Query("SELECT b.feed.id FROM Bookmark b WHERE b.student.id = :studentId AND b.feed.id IN :feedIds")
    Set<Long> findFeedIdsByStudentIdAndFeedIdsIn(@Param("studentId") Long studentId,
                                                 @Param("feedIds") List<Long> feedIds);
}
