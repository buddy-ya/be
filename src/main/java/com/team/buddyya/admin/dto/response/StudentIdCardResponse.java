package com.team.buddyya.admin.dto.response;

import com.team.buddyya.certification.domain.StudentIdCard;

public record StudentIdCardResponse(

        Long id,
        Long studentId,
        String imageUrl
) {

    public static StudentIdCardResponse from(StudentIdCard studentIdCard) {
        return new StudentIdCardResponse(
                studentIdCard.getId(),
                studentIdCard.getStudent().getId(),
                studentIdCard.getImageUrl()
        );
    }
}
