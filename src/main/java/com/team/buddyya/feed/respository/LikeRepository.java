package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByStudentIdAndFeedId(Long studentId, Long feedId);

    boolean existsByStudentIdAndFeedId(Long studentId, Long feedId);
}
