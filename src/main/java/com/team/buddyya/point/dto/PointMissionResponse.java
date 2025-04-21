package com.team.buddyya.point.dto;

import com.team.buddyya.certification.domain.RegisteredPhone;

public record PointMissionResponse(
        boolean todayAttended,
        int totalMissionPoint
) {

    public static PointMissionResponse from(RegisteredPhone registeredPhone, int totalMissionPoint) {
        return new PointMissionResponse(registeredPhone.isTodayAttended(), totalMissionPoint);
    }
}

