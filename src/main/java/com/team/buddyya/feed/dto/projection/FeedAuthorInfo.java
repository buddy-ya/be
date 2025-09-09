package com.team.buddyya.feed.dto.projection;

import com.team.buddyya.student.domain.Role;

public record FeedAuthorInfo(
        Long id,
        String name,
        String country,
        Role role,
        String characterProfileImage,
        boolean isCertificated,
        boolean isDeleted,
        String universityName
) {
}
