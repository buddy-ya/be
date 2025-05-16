package com.team.buddyya.event.dto;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;

public record CouponResponse(Integer point,
                             int pointChange) {

    public static CouponResponse from(Point point, PointType pointType) {
        return new CouponResponse(point.getCurrentPoint(), pointType.getPointChange());
    }
}
