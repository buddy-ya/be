package com.team.buddyya.student.dto.request;

import java.util.List;

public record OnBoardingRequest(
        String name,
        String country,
        Boolean isKorean,
        Boolean isNotificationEnabled,
        String phoneNumber,
        String gender,
        String university,
        List<String> majors,
        List<String> languages,
        List<String> interests
) {}
