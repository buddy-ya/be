package com.team.buddyya.feed.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.team.buddyya.common.domain.CreatedTime;
import com.team.buddyya.student.domain.Student;
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
@Table(name = "like")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Like extends CreatedTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public Like(Feed feed, Student student) {
        this.feed = feed;
        this.student = student;
    }

    @PrePersist
    private void prePersist() {
        feed.increaseLikeCount();
    }

    @PreRemove
    private void PreRemove() {
        feed.decreaseLikeCount();
    }
}
