package com.team.buddyya.feed.dto.request.feed;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record FeedUpdateRequest(
        String title,
        String content,
        String category,
        boolean isProfileVisible,
        List<MultipartFile> images
) {
}
