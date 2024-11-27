package com.team.buddyya.feed.dto.request.feed;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FeedCreateRequest(
        String title,
        String content,
        String category,
        List<MultipartFile> images
) {
}