package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;

public record CreateChatRequestResponse(Integer point, Integer pointChange) {

    public static CreateChatRequestResponse from(Point point) {
        return new CreateChatRequestResponse(point.getCurrentPoint(), PointType.CHAT_REQUEST.getPointChange());
    }
}
