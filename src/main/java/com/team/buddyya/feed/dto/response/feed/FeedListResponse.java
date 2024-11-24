package com.team.buddyya.feed.dto.response.feed;

import com.team.buddyya.feed.domain.Feed;
import java.util.List;
import org.springframework.data.domain.Page;

public record FeedListResponse(
        List<FeedResponse> feeds,
        int currentPage,
        int totalPages,
        boolean hasNext
) {

    public static FeedListResponse from(List<FeedResponse> feeds, Page<Feed> feedInfo) {
        return new FeedListResponse(
                feeds,
                feedInfo.getNumber(),
                feedInfo.getTotalPages(),
                feedInfo.hasNext()
        );
    }
}
