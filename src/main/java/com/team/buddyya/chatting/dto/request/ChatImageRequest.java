package com.team.buddyya.chatting.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ChatImageRequest(
        List<MultipartFile> images,
        Long tempId
) {
}
