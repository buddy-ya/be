package com.team.buddyya.feed.dto.request.feed;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record FeedCreateRequest(
        String title,
        String content,
        String university,
        String category,
        List<MultipartFile> images
) {
}
