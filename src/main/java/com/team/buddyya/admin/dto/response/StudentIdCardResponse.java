package com.team.buddyya.admin.dto.response;

import com.team.buddyya.certification.domain.StudentIdCard;

import java.time.LocalDateTime;

public record StudentIdCardResponse(
        Long id,
        Long userId,
        String imageUrl,
        String university,
        LocalDateTime updatedDate
) {

    public static StudentIdCardResponse from(StudentIdCard studentIdCard) {
        return new StudentIdCardResponse(
                studentIdCard.getId(),
                studentIdCard.getStudent().getId(),
                studentIdCard.getImageUrl(),
                studentIdCard.getStudent().getUniversity().getUniversityName(),
                studentIdCard.getUpdatedDate()
        );
    }
}
