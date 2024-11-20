package com.team.buddyya.feed.dto.response;

import com.team.buddyya.feed.domain.Feed;
import java.util.List;
import org.springframework.data.domain.Page;

public record FeedListResponse(
        List<FeedListItemResponse> feeds,
        int currentPage,
        int totalPages,
        boolean hasNext
) {

    public static FeedListResponse from(Page<Feed> feeds, List<FeedListItemResponse> feedResponses) {
        return new FeedListResponse(
                feedResponses,
                feeds.getNumber(),
                feeds.getTotalPages(),
                feeds.hasNext()
        );
    }
}
