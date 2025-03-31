package com.team.buddyya.feed.dto.request.feed;

import com.team.buddyya.student.domain.University;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FeedCreateRequest(
        String title,
        String content,
        String university,
        String category,
        List<MultipartFile> images
) {
}
