package com.team.buddyya.point.dto;

import com.team.buddyya.point.domain.Point;

public record PointRewardResponse(
        Integer currentPoint,
        int pointChange,
        boolean participated
) {

    public static PointRewardResponse from(Integer currentPoint, int pointChange, boolean participated) {
        return new PointRewardResponse(currentPoint, pointChange, participated);
    }
}
