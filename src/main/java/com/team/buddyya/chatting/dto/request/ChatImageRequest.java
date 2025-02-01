package com.team.buddyya.chatting.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ChatImageRequest(
        MultipartFile image,
        Long tempId
) {
}
