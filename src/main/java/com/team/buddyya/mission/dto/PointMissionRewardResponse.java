package com.team.buddyya.mission.dto;

import com.team.buddyya.point.domain.Point;

public record PointMissionRewardResponse(
        Integer point,
        int pointChange,
        boolean todayAttended,
        int totalMissionPoint
) {

    public static PointMissionRewardResponse from(Point point, int pointChange, int totalMissionPoint) {
        return new PointMissionRewardResponse(point.getCurrentPoint(), pointChange, true, totalMissionPoint);
    }
}
