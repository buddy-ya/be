package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedLike;
import com.team.buddyya.student.domain.Student;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    Optional<FeedLike> findByStudentAndFeed(Student student, Feed feed);

    boolean existsByStudentAndFeed(Student student, Feed feed);

    @Query("SELECT fl.feed.id FROM FeedLike fl WHERE fl.student.id = :studentId AND fl.feed.id IN :feedIds")
    Set<Long> findFeedIdsByStudentIdAndFeedIdsIn(@Param("studentId") Long studentId,
                                                 @Param("feedIds") List<Long> feedIds);
}
