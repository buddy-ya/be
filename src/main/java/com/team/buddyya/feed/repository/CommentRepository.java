package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByFeedId(Long feedId);
}
