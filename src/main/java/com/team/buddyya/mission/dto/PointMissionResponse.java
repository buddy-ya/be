package com.team.buddyya.mission.dto;

import com.team.buddyya.certification.domain.RegisteredPhone;

public record PointMissionResponse(
        boolean hasCertificated,
        boolean todayAttended,
        int totalMissionPoint
) {

    public static PointMissionResponse from(RegisteredPhone registeredPhone, int totalMissionPoint) {
        return new PointMissionResponse(registeredPhone.getHasCertificated(), registeredPhone.isTodayAttended(), totalMissionPoint);
    }
}

