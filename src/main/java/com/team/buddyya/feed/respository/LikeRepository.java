package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.Like;
import com.team.buddyya.student.domain.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByStudentAndFeed(Student student, Feed feed);

    boolean existsByStudentAndFeed(Student student, Feed feed);
}
