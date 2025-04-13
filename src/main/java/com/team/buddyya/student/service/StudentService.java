package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.auth.repository.AuthTokenRepository;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.repository.StudentEmailRepository;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.match.repository.MatchRequestRepository;
import com.team.buddyya.notification.repository.ExpoTokenRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.repository.PointRepository;
import com.team.buddyya.point.repository.PointStatusRepository;
import com.team.buddyya.point.service.FindPointService;
import com.team.buddyya.point.service.UpdatePointService;
import com.team.buddyya.student.domain.*;
import com.team.buddyya.student.dto.request.MyPageUpdateRequest;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.request.UpdateProfileImageRequest;
import com.team.buddyya.student.dto.response.BlockResponse;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.BlockRepository;
import com.team.buddyya.student.repository.MatchingProfileRepository;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    private final FindPointService findPointService;
    private final PointRepository pointRepository;
    private final PointStatusRepository pointStatusRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final MatchingProfileRepository matchingProfileRepository;
    private final UpdatePointService updatePointService;
    private final NotificationService notificationService;

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
        MatchingProfile matchingProfile = getMatchingProfile(student);
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

            case "introduction":
                updateMatchingProfileIntroduction(request.values(), matchingProfile);
                break;

            case "activity":
                updateMatchingProfileActivity(request.values(), matchingProfile);
                break;

            default:
                throw new StudentException(StudentExceptionType.UNSUPPORTED_UPDATE_KEY);
        }
        Point point = findPointService.findByStudent(student);
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        int totalUnreadCount = calculateTotalUnreadCount(student);
        return UserResponse.fromUserInfo(student, isStudentIdCardRequested, point, totalUnreadCount, matchingProfile);
    }

    public UserResponse updateUserProfileImage(StudentInfo studentInfo, boolean isDefault, UpdateProfileImageRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Point point = findPointService.findByStudent(student);
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        int totalUnreadCount = calculateTotalUnreadCount(student);
        MatchingProfile matchingProfile = getMatchingProfile(student);
        if (isDefault) {
            profileImageService.updateUserProfileImage(student, USER_PROFILE_DEFAULT_IMAGE.getUrl());
            return UserResponse.fromUserInfo(student, isStudentIdCardRequested, point, totalUnreadCount, matchingProfile);
        }
        String imageUrl = s3UploadService.uploadFile(PROFILE_IMAGE.getDirectoryName(), request.profileImage());
        profileImageService.updateUserProfileImage(student, imageUrl);
        return UserResponse.fromUserInfo(student, isStudentIdCardRequested, point, totalUnreadCount, matchingProfile);
    }

    private void updateMatchingProfileIntroduction(List<String> values, MatchingProfile matchingProfile) {
        if (values.size() != 1) {
            throw new StudentException(StudentExceptionType.INVALID_INTRODUCTION_UPDATE_REQUEST);
        }
        matchingProfile.updateIntroduction(values.get(0));
    }

    private void updateMatchingProfileActivity(List<String> values, MatchingProfile matchingProfile) {
        if (values.size() != 1) {
            throw new StudentException(StudentExceptionType.INVALID_ACTIVITY_UPDATE_REQUEST);
        }
        matchingProfile.updateActivity(values.get(0));
    }

    public UserResponse getUserInfo(StudentInfo studentInfo, Long userId) {
        Student student = findStudentService.findByStudentId(userId);
        MatchingProfile matchingProfile = getMatchingProfile(student);
        if (!studentInfo.id().equals(userId)) {
            return UserResponse.fromOtherUserInfo(student, matchingProfile);
        }
//        checkAttendanceAndReward(student);
        Point point = findPointService.findByStudent(student);
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        int totalUnreadCount = calculateTotalUnreadCount(student);
        return UserResponse.fromUserInfo(student, isStudentIdCardRequested, point, totalUnreadCount, matchingProfile);
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
        pointRepository.findByStudent(student).ifPresent(point -> {
            pointStatusRepository.deleteAllByPoint(point);
            pointRepository.delete(point);
        });
        profileImageService.setDefaultProfileImage(student);
        matchRequestRepository.findByStudentId(student.getId())
                .ifPresent(matchRequest -> matchRequestRepository.deleteByStudent(student));
        studentEmailRepository.deleteByEmail(student.getEmail());
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        registeredPhone.updateHasWithDrawn(true);
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

    public void logout(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        if (student.getExpoToken() != null) {
            expoTokenRepository.delete(student.getExpoToken());
        }
        student.getAvatar().setLoggedOut(true);
    }

    private int calculateTotalUnreadCount(Student student) {
        return (int) student.getChatroomStudents()
                .stream()
                .filter(chatroomStudent -> !chatroomStudent.getIsExited())
                .map(ChatroomStudent::getUnreadCount)
                .filter(count -> count > 0)
                .count();
    }

    private MatchingProfile getMatchingProfile(Student student) {
        return matchingProfileRepository.findByStudent(student)
                .orElseThrow(() -> new StudentException(StudentExceptionType.MATCHING_PROFILE_NOT_FOUND));
    }

    public void checkAttendanceAndReward(Student student) {
        RegisteredPhone registeredPhone = registeredPhoneRepository.findByPhoneNumber(student.getPhoneNumber())
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
        if (!registeredPhone.isTodayAlreadyChecked()) {
            updatePointService.updatePoint(student, PointType.EVENT_REWARD);
            registeredPhone.updateLastAttendanceDateToToday();
            notificationService.sendDailyAttendanceNotification(student);
        }
    }
}
