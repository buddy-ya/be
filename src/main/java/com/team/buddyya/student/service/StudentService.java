package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.auth.repository.AuthTokenRepository;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.repository.StudentEmailRepository;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.notification.repository.ExpoTokenRepository;
import com.team.buddyya.student.domain.*;
import com.team.buddyya.student.dto.request.MyPageUpdateRequest;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.request.UpdateProfileImageRequest;
import com.team.buddyya.student.dto.response.BlockResponse;
import com.team.buddyya.student.dto.response.UniversityResponse;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.*;

import com.team.buddyya.student.repository.BlockRepository;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.buddyya.common.domain.S3DirectoryName.PROFILE_IMAGE;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.USER_PROFILE_DEFAULT_IMAGE;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentInterestService studentInterestService;
    private final StudentLanguageService studentLanguageService;
    private final FindStudentService findStudentService;
    private final ProfileImageService profileImageService;
    private final S3UploadService s3UploadService;
    private final UniversityRepository universityRepository;
    private final StudentIdCardRepository studentIdCardRepository;
    private final BlockRepository blockRepository;
    private final AuthTokenRepository authTokenRepository;
    private final ExpoTokenRepository expoTokenRepository;
    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final StudentEmailRepository studentEmailRepository;
    private final StudentMajorRepository studentMajorRepository;
    private final StudentLanguageRepository studentLanguageRepository;
    private final StudentInterestRepository studentInterestRepository;

    private static final String BLOCK_SUCCESS_MESSAGE = "차단이 성공적으로 완료되었습니다.";

    public Student createStudent(OnBoardingRequest request) {
        University university = universityRepository.findByUniversityName(request.university())
                .orElseThrow(() -> new StudentException(StudentExceptionType.UNIVERSITY_NOT_FOUND));
        CharacterProfileImage randomImage = CharacterProfileImage.getRandomProfileImage();
        Student student = Student.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .country(request.country())
                .isKorean(request.isKorean())
                .role(Role.STUDENT)
                .university(university)
                .gender(Gender.fromValue(request.gender()))
                .characterProfileImage(randomImage.getUrl())
                .build();
        return studentRepository.save(student);
    }

    public UserResponse updateUser(StudentInfo studentInfo, MyPageUpdateRequest request) {
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
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        return UserResponse.from(student, isStudentIdCardRequested);
    }

    public UserResponse updateUserProfileImage(StudentInfo studentInfo, boolean isDefault, UpdateProfileImageRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        if (isDefault) {
            profileImageService.updateUserProfileImage(student, USER_PROFILE_DEFAULT_IMAGE.getUrl());
            return UserResponse.from(student, isStudentIdCardRequested);
        }
        String imageUrl = s3UploadService.uploadFile(PROFILE_IMAGE.getDirectoryName(), request.profileImage());
        profileImageService.updateUserProfileImage(student, imageUrl);
        return UserResponse.from(student, isStudentIdCardRequested);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(StudentInfo studentInfo, Long userId) {
        Student student = findStudentService.findByStudentId(userId);
        if (studentInfo.id() != userId) {
            return UserResponse.from(student);
        }
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        return UserResponse.from(student, isStudentIdCardRequested);
    }

    public void updateStudentCertification(Student student) {
        student.updateIsCertificated(true);
    }

    @Transactional(readOnly = true)
    public boolean isDuplicateStudentEmail(String email) {
        return studentRepository.findByEmail(email)
                .isPresent();
    }

    public void deleteStudent(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        if (student.getAuthToken() != null) {
            authTokenRepository.delete(student.getAuthToken());
        }
        if (student.getExpoToken() != null) {
            expoTokenRepository.delete(student.getExpoToken());
        }
        if (student.getStudentIdCard() != null) {
            studentIdCardRepository.delete(student.getStudentIdCard());
        }
        profileImageService.setDefaultProfileImage(student);
        registeredPhoneRepository.deleteByPhoneNumber(student.getPhoneNumber());
        studentEmailRepository.deleteByEmail(student.getEmail());
        student.markAsDeleted();
    }

    public BlockResponse blockStudent(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new StudentException(StudentExceptionType.CANNOT_BLOCK_SELF);
        }
        Student blocker = findStudentService.findByStudentId(blockerId);
        Student blocked = findStudentService.findByStudentId(blockedId);
        if (blockRepository.existsByBlockerAndBlockedStudentId(blocker, blockedId)) {
            throw new StudentException(StudentExceptionType.ALREADY_BLOCKED);
        }
        blockRepository.save(Block.builder()
                .blocker(blocker)
                .blockedStudentId(blockedId)
                .build());
        blockRepository.save(Block.builder()
                .blocker(blocked)
                .blockedStudentId(blockerId)
                .build());
        return BlockResponse.from(BLOCK_SUCCESS_MESSAGE);
    }

    @Transactional(readOnly = true)
    public List<UniversityResponse> getActiveUniversities() {
        return universityRepository.findAll().stream()
                .filter(University::getIsActive)
                .map(UniversityResponse::from)
                .collect(Collectors.toList());
    }

}
