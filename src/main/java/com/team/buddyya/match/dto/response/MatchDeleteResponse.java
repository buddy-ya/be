package com.team.buddyya.match.dto.response;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;

public record MatchDeleteResponse(Integer point, Integer pointChange) {

    public static MatchDeleteResponse from(Point point, PointType pointType) {
        return new MatchDeleteResponse(point.getCurrentPoint(), pointType.getPointChange());
    }
}
