package com.team.buddyya.certification.dto.response;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;

public record CertificationResponse(Integer point, Integer pointChange) {

    public static CertificationResponse from(Point point, PointType pointType) {
        return new CertificationResponse(point.getCurrentPoint(), pointType.getPointChange());
    }
}
