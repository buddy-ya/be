package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.request.MyPageUpdateInterestsRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateLanguagesRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateNameRequest;
import com.team.buddyya.student.dto.response.MyPageResponse;
import com.team.buddyya.student.dto.response.MyPageUpdateResponse;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private static final String UPDATE_SUCCESS_MESSAGE = "학생 정보가 성공적으로 업데이트되었습니다.";

    private final StudentRepository studentRepository;
    private final StudentInterestService studentInterestService;
    private final StudentLanguageService studentLanguageService;

    public MyPageUpdateResponse updateInterests(StudentInfo studentInfo, MyPageUpdateInterestsRequest request) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        studentInterestService.updateStudentInterests(request.interests(), student);
        return new MyPageUpdateResponse(UPDATE_SUCCESS_MESSAGE);
    }

    public MyPageUpdateResponse updateLanguages(StudentInfo studentInfo, MyPageUpdateLanguagesRequest request) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        studentLanguageService.updateStudentLanguages(request.languages(), student);
        return new MyPageUpdateResponse(UPDATE_SUCCESS_MESSAGE);
    }

    public MyPageUpdateResponse updateName(StudentInfo studentInfo, MyPageUpdateNameRequest request) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        student.updateName(request.name());
        return new MyPageUpdateResponse(UPDATE_SUCCESS_MESSAGE);
    }

    public MyPageResponse getMyPage(StudentInfo studentInfo) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        return MyPageResponse.from(student);
    }
}
