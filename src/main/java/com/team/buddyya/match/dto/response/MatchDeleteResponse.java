package com.team.buddyya.match.dto.response;

import com.team.buddyya.student.domain.Point;

public record MatchDeleteResponse(Integer point) {
    public static MatchDeleteResponse from(Point point) {
        return new MatchDeleteResponse(point.getCurrentPoint());
    }
}
