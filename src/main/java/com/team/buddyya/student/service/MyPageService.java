package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.request.MyPageUpdateRequest;
import com.team.buddyya.student.dto.request.UpdateProfileImageRequest;
import com.team.buddyya.student.dto.response.MyPageUpdateResponse;
import com.team.buddyya.student.dto.response.UserProfileResponse;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.buddyya.common.domain.S3DirectoryName.PROFILE_IMAGE;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.USER_PROFILE_DEFAULT_IMAGE;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private static final String UPDATE_SUCCESS_MESSAGE = "학생 정보가 성공적으로 업데이트되었습니다.";

    private final StudentInterestService studentInterestService;
    private final StudentLanguageService studentLanguageService;
    private final FindStudentService findStudentService;
    private final ProfileImageService profileImageService;
    private final StudentIdCardRepository studentIdCardRepository;
    private final S3UploadService s3UploadService;

    public MyPageUpdateResponse updateUser(StudentInfo studentInfo, MyPageUpdateRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());

        switch (request.key()) {
            case "interests":
                studentInterestService.updateStudentInterests(request.values(), student);
                break;

            case "languages":
                studentLanguageService.updateStudentLanguages(request.values(), student);
                break;

            case "name":
                if (request.values().size() != 1) {
                    throw new StudentException(StudentExceptionType.INVALID_NAME_UPDATE_REQUEST);
                }
                student.updateName(request.values().get(0));
                break;

            default:
                throw new StudentException(StudentExceptionType.UNSUPPORTED_UPDATE_KEY);
        }

        return MyPageUpdateResponse.from(UPDATE_SUCCESS_MESSAGE);
    }

    public MyPageUpdateResponse updateUserProfileImage(StudentInfo studentInfo, boolean isDefault, UpdateProfileImageRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        if (isDefault) {
            profileImageService.updateUserProfileImage(student, USER_PROFILE_DEFAULT_IMAGE.getUrl());
            return MyPageUpdateResponse.from(UPDATE_SUCCESS_MESSAGE);
        }
        String imageUrl = s3UploadService.uploadFile(PROFILE_IMAGE.getDirectoryName(), request.profileImage());
        profileImageService.updateUserProfileImage(student, imageUrl);
        return MyPageUpdateResponse.from(UPDATE_SUCCESS_MESSAGE);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(StudentInfo studentInfo, Long userId) {
        Student student = findStudentService.findByStudentId(userId);
        if (studentInfo.id() != userId) {
            return UserProfileResponse.from(student);
        }
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        return UserProfileResponse.from(student, isStudentIdCardRequested);
    }
}
