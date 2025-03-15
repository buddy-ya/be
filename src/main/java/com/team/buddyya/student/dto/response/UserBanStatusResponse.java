package com.team.buddyya.student.dto.response;

import java.time.LocalDateTime;

public record UserBanStatusResponse(
        boolean isBanned,
        LocalDateTime banEndTime
) {

    public static UserBanStatusResponse from(boolean isBanned, LocalDateTime banEndTime) {
        return new UserBanStatusResponse(isBanned, banEndTime);
    }
}