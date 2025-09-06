package com.team.buddyya.job.feed.comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentJobDTO {
    private Long studentId;
    private Long feedId;
    private String content;
    private Long parentId;
}
