package com.team.buddyya.point.dto;

public record PointMissionRewardResponse(
        Integer currentPoint,
        int pointChange
) {

    public static PointMissionRewardResponse from(Integer currentPoint, int pointChange) {
        return new PointMissionRewardResponse(currentPoint, pointChange);
    }
}
