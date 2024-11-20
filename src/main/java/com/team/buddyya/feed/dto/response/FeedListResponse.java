package com.team.buddyya.feed.dto.response;

import java.util.List;

public record FeedListResponse(
        List<FeedListItemResponse> feeds,
        int currentPage,
        int totalPages,
        boolean hasNext
) {
}
