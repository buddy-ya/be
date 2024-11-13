package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.*;
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

    public OnBoardingResponse onboard(OnBoardingRequest request) {
        Student student = studentService.createStudent(request);
        avatarService.createAvatar(request, student);
        studentInterestService.createStudentInterests(request, student);
        studentLanguageService.createStudentLanguages(request, student);
        return OnBoardingResponse.from(student);
    }
}
