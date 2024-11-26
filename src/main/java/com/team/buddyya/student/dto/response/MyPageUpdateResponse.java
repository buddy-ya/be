package com.team.buddyya.student.dto.response;

public record MyPageUpdateResponse(String message) {

    public static MyPageUpdateResponse from(String message) {
        return new MyPageUpdateResponse(message);
    }
}
