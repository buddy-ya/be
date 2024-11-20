package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
