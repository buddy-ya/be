package com.team.buddyya.student.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileImageRequest(
        MultipartFile profileImage
) {
}
