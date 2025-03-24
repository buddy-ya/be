package com.team.buddyya.point.domain;

import com.team.buddyya.point.exception.PointException;
import com.team.buddyya.point.exception.PointExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PointType {

    SIGNUP("signup", +100),
    UNIVERSITY_AUTH("university_auth",1),
    CHAT_REQUEST("chat_request", -1),
    MATCH_REQUEST("match_request", -1),
    CANCEL_MATCH_REQUEST("cancel_match_request", +1);

    private final String displayName;
    private final int pointChange;

    PointType(String displayName, int pointChange) {
        this.displayName = displayName;
        this.pointChange = pointChange;
    }

    public static PointType fromValue(String displayName) {
        return Arrays.stream(PointType.values())
                .filter(type -> type.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new PointException(PointExceptionType.INVALID_POINT_TYPE));
    }
}
