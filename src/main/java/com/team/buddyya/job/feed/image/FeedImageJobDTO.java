package com.team.buddyya.job.feed.image;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedImageJobDTO {
    private Long feedId;
    private String url;
}
