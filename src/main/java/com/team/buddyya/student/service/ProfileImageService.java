package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.ProfileDefaultImage;
import com.team.buddyya.student.domain.ProfileImage;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;

    public void saveRandomProfileImage(Student student) {
        ProfileDefaultImage randomImage = ProfileDefaultImage.getRandomProfileImage();
        ProfileImage profileImage = ProfileImage.builder()
                .student(student)
                .url(randomImage.getUrl())
                .build();
        profileImage.setStudent(student);
        profileImageRepository.save(profileImage);
    }

    public void updateProfileDefaultImage(Student student, String profileImageKey) {
        ProfileDefaultImage defaultImage = ProfileDefaultImage.fromValue(profileImageKey);
        ProfileImage profileImage = student.getProfileImage();
        profileImage.updateUrl(defaultImage.getUrl());
    }
}

