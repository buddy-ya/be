package com.team.buddyya.student.dto.request;

import java.util.List;

public record MyPageUpdateRequest(
        String key,
        List<String> values
) {
}
