package com.team.buddyya.point.dto;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.student.domain.Student;

public record PointMissionResponse(
        boolean hasCertificated,
        boolean todayAttended,
        int totalMissionPoint
) {

    public static PointMissionResponse from(RegisteredPhone registeredPhone, int totalMissionPoint) {
        return new PointMissionResponse(registeredPhone.getHasCertificated(), registeredPhone.isTodayAttended(), totalMissionPoint);
    }
}

