package com.team.buddyya.student.dto.request;

import com.team.buddyya.student.domain.Gender;

import java.util.List;

public record OnBoardingRequest(
        String name,
        String major,
        String country,
        Boolean korean,
        Boolean notificationEnabled,
        String phoneNumber,
        Gender gender,
        String university,
        List<String> languages,
        List<String> interests
) {}
