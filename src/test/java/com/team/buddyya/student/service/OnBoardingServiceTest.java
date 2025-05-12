package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.AuthToken;
import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.auth.repository.AuthTokenRepository;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.service.PointService;
import com.team.buddyya.student.domain.*;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.repository.MatchingProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnBoardingServiceTest {

    @InjectMocks
    private OnBoardingService onBoardingService;

    @Mock private StudentService studentService;
    @Mock private AvatarService avatarService;
    @Mock private StudentInterestService studentInterestService;
    @Mock private StudentLanguageService studentLanguageService;
    @Mock private StudentMajorService studentMajorService;
    @Mock private ProfileImageService profileImageService;
    @Mock private PointService pointService;
    @Mock private MatchingProfileRepository matchingProfileRepository;
    @Mock private AuthTokenRepository authTokenRepository;
    @Mock private JwtUtils jwtUtils;

    private Student student;

    @BeforeEach
    void setup() {
        student = mock(Student.class);
        when(student.getId()).thenReturn(1L);
        when(student.getName()).thenReturn("John");
        when(student.getIsCertificated()).thenReturn(false);
        when(student.getIsKorean()).thenReturn(false);
        when(student.checkAndUpdateBanStatus()).thenReturn(false);
        when(student.getRole()).thenReturn(Role.STUDENT);
        when(student.getUniversity()).thenReturn(University.builder().universityName("sju").isActive(true).build());
        when(student.getGender()).thenReturn(Gender.MALE);
        when(student.getProfileImage()).thenReturn(ProfileImage.builder().url(UserProfileDefaultImage.USER_PROFILE_DEFAULT_IMAGE.getUrl()).build());
    }

    @Test
    void 온보딩_로직이_정상적으로_수행된다() {
        // given
        OnBoardingRequest request = new OnBoardingRequest(
                "John",
                "us",
                false,
                true,
                "01012345678",
                "male",
                "sju",
                List.of("humanities"),
                List.of("ko"),
                List.of("kpop")
        );

        Point point = Point.builder()
                .student(student)
                .currentPoint(100)
                .build();

        when(studentService.createStudent(any())).thenReturn(student);
        when(jwtUtils.createAccessToken(any(TokenInfoRequest.class))).thenReturn("access-token");
        when(jwtUtils.createRefreshToken(any(TokenInfoRequest.class))).thenReturn("refresh-token");
        when(pointService.createPoint(any())).thenReturn(point);

        // when
        UserResponse response = onBoardingService.onboard(request);

        // then
        assertThat(response.name()).isEqualTo("John");
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.point()).isEqualTo(100);

        verify(studentService).createStudent(any());
        verify(avatarService).createAvatar(request, student);
        verify(studentMajorService).createStudentMajors(request.majors(), student);
        verify(studentInterestService).createStudentInterests(request.interests(), student);
        verify(studentLanguageService).createStudentLanguages(request.languages(), student);
        verify(profileImageService).saveDefaultProfileImage(student);
        verify(matchingProfileRepository).save(any(MatchingProfile.class));
        verify(authTokenRepository).save(any(AuthToken.class));
    }
}
