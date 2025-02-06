package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedLike;
import com.team.buddyya.student.domain.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    Optional<FeedLike> findByStudentAndFeed(Student student, Feed feed);

    boolean existsByStudentAndFeed(Student student, Feed feed);
}
