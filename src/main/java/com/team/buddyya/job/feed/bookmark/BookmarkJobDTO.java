package com.team.buddyya.job.feed.bookmark;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkJobDTO {
    private Long feedId;
    private Long studentId;
}
