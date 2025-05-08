package com.team.buddyya.point.domain;

import com.team.buddyya.point.exception.PointException;
import com.team.buddyya.point.exception.PointExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PointType {

    NEW_SIGNUP("new_signup", 50, PointChangeType.EARN),
    SIGNUP("signup", 100, PointChangeType.EARN),
    UNIVERSITY_AUTH("university_auth", 1, PointChangeType.EARN),
    INVITATION_EVENT("invitation_event", 100, PointChangeType.EARN),
    CHAT_REQUEST("chat_request", -15, PointChangeType.DEDUCT),
    MATCH_REQUEST("match_request", -35, PointChangeType.DEDUCT),
    CANCEL_MATCH_REQUEST("cancel_match_request", 35, PointChangeType.EARN),
    NO_POINT_CHANGE("no_point_change", 0, PointChangeType.NONE),
    REJECTED_CHAT_REQUEST("rejected_chat_request", 15, PointChangeType.EARN),
    CHATROOM_NO_RESPONSE_REFUND("chatroom_no_response_refund", 35, PointChangeType.EARN),
    MISSION_CERTIFICATION_REWARD("mission_certification_reward", 100, PointChangeType.MISSION),
    MISSION_VISIT_REWARD("mission_visit_reward", 10, PointChangeType.MISSION),
    EVENT_REWARD("event_reward", 10, PointChangeType.EARN);

    private final String displayName;
    private final int pointChange;
    private final PointChangeType changeType;

    PointType(String displayName, int pointChange, PointChangeType changeType) {
        this.displayName = displayName;
        this.pointChange = pointChange;
        this.changeType = changeType;
    }

    public static PointType fromValue(String displayName) {
        return Arrays.stream(PointType.values())
                .filter(type -> type.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new PointException(PointExceptionType.INVALID_POINT_TYPE));
    }
}
