package com.team.buddyya.feed.dto.response.feed;

import com.team.buddyya.feed.domain.Feed;
import java.util.List;
import org.springframework.data.domain.Page;

public record FeedListResponse(
        List<FeedListItemResponse> feeds,
        int currentPage,
        int totalPages,
        boolean hasNext
) {

    public static FeedListResponse from(Page<Feed> feedInfo, List<FeedListItemResponse> feeds) {
        return new FeedListResponse(
                feeds,
                feedInfo.getNumber(),
                feedInfo.getTotalPages(),
                feedInfo.hasNext()
        );
    }
}
