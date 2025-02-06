package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.CommentLike;
import com.team.buddyya.student.domain.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentAndStudentId(Comment comment, Long studentId);

    Optional<CommentLike> findByCommentAndStudent(Comment comment, Student student);
}
