package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.AuthToken;
import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.auth.repository.AuthTokenRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.OnBoardingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OnBoardingService {

    private final StudentService studentService;
    private final AvatarService avatarService;
    private final StudentInterestService studentInterestService;
    private final StudentLanguageService studentLanguageService;
    private final StudentMajorService studentMajorService;
    private final AuthTokenRepository authTokenRepository;
    private final JwtUtils jwtUtils;

    public OnBoardingResponse onboard(OnBoardingRequest request) {
        Student student = studentService.createStudent(request);
        String accessToken = jwtUtils.createAccessToken(new TokenInfoRequest(student.getId()));
        String refreshToken = createAndSaveToken(student);
        avatarService.createAvatar(request, student);
        studentMajorService.createStudentMajors(request,student);
        studentInterestService.createStudentInterests(request, student);
        studentLanguageService.createStudentLanguages(request, student);
        return OnBoardingResponse.from(accessToken, refreshToken);
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
}
