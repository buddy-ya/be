package com.team.buddyya.admin.service;

import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.AdminReportResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardListResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.admin.dto.response.StudentVerificationResponse;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.report.domain.ReportImage;
import com.team.buddyya.report.repository.ReportImageRepository;
import com.team.buddyya.report.repository.ReportRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
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
    private final FindStudentService findStudentService;
    private final S3UploadService s3UploadService;
    private final NotificationService notificationService;
    private final StudentIdCardRepository studentIdCardRepository;
    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;

    @Transactional(readOnly = true)
    public StudentIdCardListResponse getStudentIdCards() {
        List<StudentIdCardResponse> studentIdCards = studentIdCardRepository.findAllByOrderByCreatedDateAsc().stream()
                .map(StudentIdCardResponse::from)
                .collect(Collectors.toList());
        return StudentIdCardListResponse.from(studentIdCards);
    }

    public StudentVerificationResponse verifyStudentIdCard(StudentVerificationRequest request) {
        Student student = findStudentService.findByStudentId(request.id());
        studentIdCardRepository.deleteByStudent(student);
        s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), request.imageUrl());
        studentService.updateStudentCertification(student);
        notificationService.sendAuthorizationNotification(student, true);
        return new StudentVerificationResponse(VERIFICATION_COMPLETED_MESSAGE);
    }

    public List<AdminReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(report -> AdminReportResponse.from(report, getImageUrlsByReportId(report.getId())))
                .collect(Collectors.toList());
    }

    private List<String> getImageUrlsByReportId(Long reportId) {
        return reportImageRepository.findByReportId(reportId).stream()
                .map(ReportImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
