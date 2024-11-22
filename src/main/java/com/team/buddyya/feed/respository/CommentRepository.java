package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByFeedId(Long feedId);
}
