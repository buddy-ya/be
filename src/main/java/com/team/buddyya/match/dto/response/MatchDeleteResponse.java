package com.team.buddyya.match.dto.response;

import com.team.buddyya.point.domain.Point;

public record MatchDeleteResponse(Integer point, Integer pointChange) {

    public static MatchDeleteResponse from(Point point, Integer pointChange) {
        return new MatchDeleteResponse(point.getCurrentPoint(), pointChange);
    }
}
