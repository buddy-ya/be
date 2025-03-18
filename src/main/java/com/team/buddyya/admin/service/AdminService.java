package com.team.buddyya.admin.service;

import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.AdminChatMessageResponse;
import com.team.buddyya.admin.dto.response.AdminReportResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.admin.dto.response.StudentVerificationResponse;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportImage;
import com.team.buddyya.report.domain.ReportType;
import com.team.buddyya.report.exception.ReportException;
import com.team.buddyya.report.repository.ReportImageRepository;
import com.team.buddyya.report.repository.ReportRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.service.FindStudentService;
import com.team.buddyya.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team.buddyya.certification.exception.CertificateExceptionType.STUDENT_ID_CARD_NOT_FOUND;
import static com.team.buddyya.common.domain.S3DirectoryName.STUDENT_ID_CARD;
import static com.team.buddyya.report.exception.ReportExceptionType.REPORT_NOT_FOUND;
import static com.team.buddyya.student.exception.StudentExceptionType.STUDENT_NOT_FOUND;

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
    private final ChatroomRepository chatroomRepository;
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<StudentIdCardResponse> getStudentIdCards() {
        return studentIdCardRepository.findAllByOrderByCreatedDateAsc().stream()
                .map(StudentIdCardResponse::from)
                .collect(Collectors.toList());
    }

    public StudentVerificationResponse verifyStudentIdCard(StudentVerificationRequest request) {
        StudentIdCard studentIdCard = studentIdCardRepository.findById(request.id())
                .orElseThrow(() -> new CertificateException(STUDENT_ID_CARD_NOT_FOUND));
        Student student = Optional.ofNullable(studentIdCard.getStudent())
                .orElseThrow(() -> new StudentException(STUDENT_NOT_FOUND));
        if (request.isApproved()) {
            s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), studentIdCard.getImageUrl());
            studentIdCardRepository.delete(studentIdCard);
            studentService.updateStudentCertification(student);
            notificationService.sendAuthorizationNotification(student, true);
            return new StudentVerificationResponse(VERIFICATION_COMPLETED_MESSAGE);
        } else {
            studentIdCard.updateRejectionReason(request.rejectionReason());
            notificationService.sendAuthorizationNotification(student, false);
            return new StudentVerificationResponse(REQUEST_REJECTED_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<AdminReportResponse> getReportsByType(ReportType type) {
        return reportRepository.findByType(type).stream()
                .map(report -> AdminReportResponse.from(report, getImageUrlsByReportId(report.getId())))
                .collect(Collectors.toList());
    }

    public void deleteReport(Long reportId) {
        List<ReportImage> reportImages = reportImageRepository.findByReportId(reportId);
        if (!reportImages.isEmpty()) {
            reportImageRepository.deleteAll(reportImages);
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException(REPORT_NOT_FOUND));
        reportRepository.delete(report);
    }


    private List<String> getImageUrlsByReportId(Long reportId) {
        return reportImageRepository.findByReportId(reportId).stream()
                .map(ReportImage::getImageUrl)
                .collect(Collectors.toList());
    }

    public void banStudent(Long studentId, int days) {
        Student student = findStudentService.findByStudentId(studentId);
        student.ban(days);
    }

    public void unbanStudent(Long studentId) {
        Student student = findStudentService.findByStudentId(studentId);
        student.unban();
    }

    public List<AdminChatMessageResponse> getAllChatMessages(Long chatroomId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        List<Chat> chats = chatRepository.findByChatroom(chatroom);
        return chats.stream()
                .map(AdminChatMessageResponse::from)
                .toList();
    }
}
