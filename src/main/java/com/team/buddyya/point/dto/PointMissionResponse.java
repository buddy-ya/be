package com.team.buddyya.point.dto;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.student.domain.Student;

public record PointMissionResponse(
        boolean isCertificated,
        boolean todayAttended,
        int totalMissionPoint
) {

    public static PointMissionResponse from(Student student, RegisteredPhone registeredPhone, int totalMissionPoint) {
        return new PointMissionResponse(student.getIsCertificated(), registeredPhone.isTodayAttended(), totalMissionPoint);
    }
}

