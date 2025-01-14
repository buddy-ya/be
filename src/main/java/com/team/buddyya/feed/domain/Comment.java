package com.team.buddyya.feed.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.team.buddyya.common.domain.BaseTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public Comment(Student student, Feed feed, String content) {
        this.student = student;
        this.feed = feed;
        this.content = content;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
        feed.getComments().add(this);
    }

    public void updateComment(String content) {
        this.content = content;
    }

    @PrePersist
    private void prePersist() {
        feed.increaseCommentCount();
    }

    @PreRemove
    private void PreRemove() {
        feed.decreaseCommentCount();
    }
}
