package com.team.buddyya.admin.dto.response;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;

public record StudentVerificationResponse(Integer point, Integer pointChange) {

    public static StudentVerificationResponse from(Point point, PointType pointType) {
        return new StudentVerificationResponse(point.getCurrentPoint(), pointType.getPointChange());
    }
}
