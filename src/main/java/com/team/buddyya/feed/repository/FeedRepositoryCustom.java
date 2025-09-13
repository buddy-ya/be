package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.dto.request.feed.FeedCursorListRequest;
import org.springframework.data.domain.Slice;

public interface FeedRepositoryCustom {

    Slice<Feed> findFeedsByCursor(FeedCursorListRequest request, Feed lastFeed);
}
