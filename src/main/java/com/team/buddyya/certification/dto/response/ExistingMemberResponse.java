package com.team.buddyya.certification.dto.response;

import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

public record ExistingMemberResponse(
        Long id,
        String name,
        String country,
        String university,
        String gender,
        String profileImageUrl,
        Boolean isCertificated,
        Boolean isStudentIdCardRequested,
        Boolean isKorean,
        List<String> majors,
        List<String> languages,
        List<String> interests,
        String phoneNumber,
        String status,
        String accessToken,
        String refreshToken) {

    public static ExistingMemberResponse from(Student student, Boolean isStudentIdCardRequested, String phoneNumber, String status, String accessToken, String refreshToken){
        return new ExistingMemberResponse(
                student.getId(),
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                student.getProfileImage().getUrl(),
                student.getIsCertificated(),
                isStudentIdCardRequested,
                student.getIsKorean(),
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                phoneNumber,
                status,
                accessToken,
                refreshToken);
    }

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
