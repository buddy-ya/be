package com.team.buddyya.feed.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_image")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class FeedImage extends CreatedTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(length = 255, nullable = false)
    private String url;

    @Builder
    public FeedImage(Feed feed, String url) {
        this.feed = feed;
        this.url = url;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
        feed.getImages().add(this);
    }
}
