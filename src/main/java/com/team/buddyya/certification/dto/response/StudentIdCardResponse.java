package com.team.buddyya.certification.dto.response;

public record StudentIdCardResponse(String studentIdCardUrl) {

    public static StudentIdCardResponse from(String studentIdCardUrl) {
        return new StudentIdCardResponse(studentIdCardUrl);
    }
}
