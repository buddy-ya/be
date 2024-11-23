package com.team.buddyya.admin.dto.response;

import java.util.List;

public record StudentIdCardListResponse(List<StudentIdCardResponse> studentIdCards) {

    public static StudentIdCardListResponse from(List<StudentIdCardResponse> studentIdCards) {
        return new StudentIdCardListResponse(studentIdCards);
    }
}
