package com.team.buddyya.student.service;

import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.ProfileDefaultImage;
import com.team.buddyya.student.domain.ProfileImage;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.repository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.team.buddyya.common.domain.S3DirectoryName.PROFILE_IMAGE;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.isDefaultUserProfileImage;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final S3UploadService s3UploadService;

    public void saveRandomProfileImage(Student student) {
        ProfileDefaultImage randomImage = ProfileDefaultImage.getRandomProfileImage();
        ProfileImage profileImage = ProfileImage.builder()
                .student(student)
                .url(randomImage.getUrl())
                .build();
        profileImageRepository.save(profileImage);
    }

    public void updateUserProfileImage(Student student, String imageUrl) {
        if (isDefaultUserProfileImage(student) == false) {
            s3UploadService.deleteFile(PROFILE_IMAGE.getDirectoryName(), student.getUserProfileImage());
        }
        student.updateUserProfileImage(imageUrl);
    }
}

