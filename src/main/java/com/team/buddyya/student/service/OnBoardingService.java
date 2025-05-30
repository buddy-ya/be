package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.AuthToken;
import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.auth.repository.AuthTokenRepository;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.service.PointService;
import com.team.buddyya.student.domain.MatchingProfile;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.repository.MatchingProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OnBoardingService {

    private final StudentService studentService;
    private final AvatarService avatarService;
    private final StudentInterestService studentInterestService;
    private final StudentLanguageService studentLanguageService;
    private final StudentMajorService studentMajorService;
    private final ProfileImageService profileImageService;
    private final PointService pointService;
    private final MatchingProfileRepository matchingProfileRepository;
    private final AuthTokenRepository authTokenRepository;
    private final JwtUtils jwtUtils;

    public UserResponse onboard(OnBoardingRequest request) {
        Student student = studentService.createStudent(request);
        String accessToken = jwtUtils.createAccessToken(new TokenInfoRequest(student.getId()));
        String refreshToken = createAndSaveToken(student);
        profileImageService.saveDefaultProfileImage(student);
        avatarService.createAvatar(request, student);
        studentMajorService.createStudentMajors(request.majors(), student);
        studentInterestService.createStudentInterests(request.interests(), student);
        studentLanguageService.createStudentLanguages(request.languages(), student);
        createMatchingProfile(student);
        Point point = pointService.createPoint(student);
        log.info("🧑‍🎓 Student id = [{}], name = [{}] onboarded successfully", student.getId(), student.getName());
        return UserResponse.fromOnboard(student, false, accessToken, refreshToken, point);
    }

    private String createAndSaveToken(Student student) {
        String refreshToken = jwtUtils.createRefreshToken(new TokenInfoRequest(student.getId()));
        AuthToken authToken = AuthToken.builder()
                .refreshToken(refreshToken)
                .student(student)
                .build();
        authTokenRepository.save(authToken);
        return refreshToken;
    }

    private void createMatchingProfile(Student student) {
        matchingProfileRepository.save(new MatchingProfile(student));
    }
}
