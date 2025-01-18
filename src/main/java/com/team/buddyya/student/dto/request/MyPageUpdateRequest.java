package com.team.buddyya.student.dto.request;

import java.util.List;


public record MyPageUpdateRequest(
        String key, // "interests", "languages", "name"
        List<String> values
) {
}
