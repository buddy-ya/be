package com.team.buddyya.feed.domain;

import com.team.buddyya.common.domain.CreatedTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_like")
@Getter
@NoArgsConstructor
public class CommentLike extends CreatedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public CommentLike(Comment comment, Student student) {
        this.comment = comment;
        this.student = student;
    }

    @PrePersist
    private void prePersist() {
        comment.increaseLikeCount();
    }

    @PreRemove
    private void preRemove() {
        comment.decreaseLikeCount();
    }
}
