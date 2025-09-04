package com.team.buddyya.job.feed;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedJobDTO {
    private String title;
    private String content;
    private boolean isProfileVisible;
    private Long studentId;
    private Long categoryId;
    private Long universityId;
}
