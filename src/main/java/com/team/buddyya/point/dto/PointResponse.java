package com.team.buddyya.point.dto;

import com.team.buddyya.point.domain.PointStatus;
import com.team.buddyya.point.domain.PointType;

import java.time.LocalDateTime;

public record PointResponse(
        Long id,
        String pointType,
        String pointChangeType,
        int pointChange,
        LocalDateTime createdDate
) {

    public static PointResponse from(PointStatus pointStatus) {
        PointType pointType = pointStatus.getPointType();
        return new PointResponse(
                pointStatus.getId(),
                pointType.getDisplayName(),
                pointType.getChangeType().getDisplayName(),
                pointType.getPointChange(),
                pointStatus.getCreatedDate()
        );
    }
}
