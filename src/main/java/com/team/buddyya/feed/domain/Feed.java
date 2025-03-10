package com.team.buddyya.feed.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.team.buddyya.common.domain.BaseTime;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Feed extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FeedImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FeedLike> feedLikes;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Bookmark> bookmarks;

    @Builder
    public Feed(String title, String content, Student student, Category category, University university) {
        this.title = title;
        this.content = content;
        this.student = student;
        this.category = category;
        this.university = university;
        this.likeCount = 0;
        this.commentCount = 0;
        this.viewCount = 0;
    }

    public void updateFeed(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount--;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void uploadFeedImages(List<FeedImage> images) {
        this.images = images;
    }

    public boolean isMine(Long studentId) {
        return this.student.getId().equals(studentId);
    }
}
