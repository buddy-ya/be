package com.team.buddyya.point.domain;

import lombok.Getter;

@Getter
public enum PointChangeType {

    EARN("earn"),
    DEDUCT("deduct"),
    NONE("none");

    private final String displayName;

    PointChangeType(String displayName) {
        this.displayName = displayName;
    }
}
