package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.point.domain.Point;

public record CreateChatRequestResponse(Integer point) {

    public static CreateChatRequestResponse from(Point point) {
        return new CreateChatRequestResponse(point.getCurrentPoint());
    }
}
