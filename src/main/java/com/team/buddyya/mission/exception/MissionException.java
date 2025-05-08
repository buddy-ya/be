package com.team.buddyya.mission.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;
import com.team.buddyya.point.exception.PointExceptionType;

public class MissionException extends BaseException {

    private final MissionExceptionType exceptionType;

    public MissionException(MissionExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}