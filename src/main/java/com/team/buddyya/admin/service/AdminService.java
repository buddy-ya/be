package com.team.buddyya.admin.service;

import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.StudentIdCardListResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.admin.dto.response.StudentVerificationResponse;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.buddyya.common.domain.S3DirectoryName.STUDENT_ID_CARD;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private static final String REQUEST_REJECTED_MESSAGE = "인증 요청이 거절되었습니다";
    private static final String ALREADY_REGISTERED_MESSAGE = "이미 등록된 학번입니다";
    private static final String VERIFICATION_COMPLETED_MESSAGE = "학생 인증이 완료되었습니다";

    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final StudentIdCardRepository studentIdCardRepository;
    private final S3UploadService s3UploadService;

    public StudentIdCardListResponse getStudentIdCards() {
        List<StudentIdCardResponse> studentIdCards = studentIdCardRepository.findAllByOrderByCreatedDateAsc().stream()
                .map(StudentIdCardResponse::from)
                .collect(Collectors.toList());
        return new StudentIdCardListResponse(studentIdCards);
    }

    public StudentVerificationResponse verifyStudentIdCard(StudentVerificationRequest request) {
        s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), request.imageUrl());
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        studentIdCardRepository.deleteByStudent(student);
        if (request.studentNumber() == null) {
            return new StudentVerificationResponse(REQUEST_REJECTED_MESSAGE);
        }
        if (studentService.isDuplicateStudentNumber(request.studentNumber(), student.getUniversity())) {
            return new StudentVerificationResponse(ALREADY_REGISTERED_MESSAGE);
        }
        studentService.updateStudentCertification(student, request.studentNumber());
        return new StudentVerificationResponse(VERIFICATION_COMPLETED_MESSAGE);
    }
}
