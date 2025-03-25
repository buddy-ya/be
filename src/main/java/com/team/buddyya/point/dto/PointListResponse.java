package com.team.buddyya.point.dto;

import com.team.buddyya.point.domain.Point;

import java.util.List;

public record PointListResponse(
        List<PointResponse> points,
        int currentPoint
) {

    public static PointListResponse from(Point point, List<PointResponse> points) {
        return new PointListResponse(
                points,
                point.getCurrentPoint()
        );
    }
}
