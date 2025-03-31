package com.team.buddyya.student.dto.response;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;

public record ValidateInvitationCodeResponse(Integer point, Integer pointChange) {

    public static ValidateInvitationCodeResponse from(Point point, PointType pointType) {
        return new ValidateInvitationCodeResponse(point.getCurrentPoint(), pointType.getPointChange());
    }
}
