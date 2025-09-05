package com.team.buddyya.job.feed.like;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedLikeJobDTO {
    private Long feedId;
    private Long studentId;
}
