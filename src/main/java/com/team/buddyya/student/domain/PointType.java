package com.team.buddyya.student.domain;

import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PointType {

    INITIAL("initial", 0),
    SIGNUP("signup", 1),
    UNIVERSITY_AUTH("university_auth",1),
    CHAT_REQUEST("chat_request", -1),
    MATCH_REQUEST("match_request", -1);

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
                .orElseThrow(() -> new StudentException(StudentExceptionType.INVALID_POINT_TYPE));
    }
}
