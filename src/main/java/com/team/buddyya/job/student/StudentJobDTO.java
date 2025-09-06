package com.team.buddyya.job.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentJobDTO {
    private final String phoneNumber;
    private final String name;
    private final String country;
    private final Boolean isCertificated;
    private final Boolean isKorean;
    private final Boolean isDeleted;
    private final Long universityId;
    private final String role;
    private final String gender;
    private final String characterProfileImage;
    private final Boolean isBanned;
}
