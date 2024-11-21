package com.team.buddyya.admin.service;

import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.admin.dto.response.StudentVerificationResponse;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team.buddyya.common.domain.S3DirectoryName.STUDENT_ID_CARD;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final StudentRepository studentRepository;
    private final StudentIdCardRepository studentIdCardRepository;
    private final S3UploadService s3UploadService;

    public List<StudentIdCardResponse> getStudentIdCards() {
        return studentIdCardRepository.findAllByOrderByCreatedDateAsc().stream()
                .map(StudentIdCardResponse::from)
                .collect(Collectors.toList());
    }

    public StudentVerificationResponse verifyStudentIdCard(StudentVerificationRequest request) {
        s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), request.imageUrl());
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        studentIdCardRepository.deleteByStudent(student);
        if(request.studentNumber() == null){
            return new StudentVerificationResponse(true);
        }
        Optional<Student> existingStudent = studentRepository.findByStudentNumberAndUniversity(request.studentNumber(), student.getUniversity());
        if (existingStudent.isPresent()) {
            return new StudentVerificationResponse(false);
        }
        student.updateIsCertificated(true);
        student.updateStudentNumber(request.studentNumber());
        return new StudentVerificationResponse(true);
    }
}
