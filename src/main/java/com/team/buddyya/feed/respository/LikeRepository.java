package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByStudentIdAndFeedId(Long studentId, Long feedId);
}
