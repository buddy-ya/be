package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.request.MyPageUpdateInterestsRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateLanguagesRequest;
import com.team.buddyya.student.dto.request.MyPageUpdateNameRequest;
import com.team.buddyya.student.dto.response.MyPageResponse;
import com.team.buddyya.student.dto.response.MyPageUpdateResponse;
import com.team.buddyya.student.dto.response.UserPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public MyPageUpdateResponse updateInterests(StudentInfo studentInfo, MyPageUpdateInterestsRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        studentInterestService.updateStudentInterests(request.interests(), student);
        return MyPageUpdateResponse.from(UPDATE_SUCCESS_MESSAGE);
    }

    public MyPageUpdateResponse updateLanguages(StudentInfo studentInfo, MyPageUpdateLanguagesRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        studentLanguageService.updateStudentLanguages(request.languages(), student);
        return MyPageUpdateResponse.from(UPDATE_SUCCESS_MESSAGE);
    }

    public MyPageUpdateResponse updateName(StudentInfo studentInfo, MyPageUpdateNameRequest request) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        student.updateName(request.name());
        return MyPageUpdateResponse.from(UPDATE_SUCCESS_MESSAGE);
    }

    public MyPageUpdateResponse updateProfileDefaultImage(StudentInfo studentInfo, String profileImageKey) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        profileImageService.updateProfileDefaultImage(student, profileImageKey);
        return MyPageUpdateResponse.from(UPDATE_SUCCESS_MESSAGE);
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        return MyPageResponse.from(student,isStudentIdCardRequested);
    }

    @Transactional(readOnly = true)
    public UserPageResponse getUserPage(long studentId) {
        Student student = findStudentService.findByStudentId(studentId);
        return UserPageResponse.from(student);
    }
}
