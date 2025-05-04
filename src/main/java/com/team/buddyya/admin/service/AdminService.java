package com.team.buddyya.admin.service;

import com.team.buddyya.admin.dto.request.BanRequest;
import com.team.buddyya.admin.dto.request.StudentVerificationRequest;
import com.team.buddyya.admin.dto.response.AdminChatMessageResponse;
import com.team.buddyya.admin.dto.response.AdminReportResponse;
import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.admin.dto.response.StudentVerificationResponse;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomType;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.service.FindPointService;
import com.team.buddyya.point.service.UpdatePointService;
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

    private static final String REQUEST_REJECTED_MESSAGE = "The verification request has been rejected.";
    private static final String VERIFICATION_COMPLETED_MESSAGE = "Student verification has been completed.";

    private final StudentService studentService;
    private final FindStudentService findStudentService;
    private final S3UploadService s3UploadService;
    private final NotificationService notificationService;
    private final UpdatePointService updatePointService;
    private final StudentIdCardRepository studentIdCardRepository;
    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    private final ChatroomRepository chatroomRepository;
    private final ChatRepository chatRepository;
    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final FindPointService findPointService;

    @Transactional(readOnly = true)
    public List<StudentIdCardResponse> getStudentIdCards() {
        return studentIdCardRepository.findUploadStudentIdCards()
                .stream()
                .map(StudentIdCardResponse::from)
                .collect(Collectors.toList());
    }

    public StudentVerificationResponse verifyStudentIdCard(StudentVerificationRequest request) {
        StudentIdCard studentIdCard = studentIdCardRepository.findById(request.id())
                .orElseThrow(() -> new CertificateException(STUDENT_ID_CARD_NOT_FOUND));
        Student student = Optional.ofNullable(studentIdCard.getStudent())
                .orElseThrow(() -> new StudentException(STUDENT_NOT_FOUND));
        Point point = findPointService.findByStudent(student);
        if (request.isApproved()) {
            return approveStudentIdCard(studentIdCard, point, student);
        }
        return rejectStudentIdCard(request, point, studentIdCard, student);
    }

    private StudentVerificationResponse approveStudentIdCard(StudentIdCard studentIdCard, Point point, Student student) {
        s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), studentIdCard.getImageUrl());
        studentIdCardRepository.delete(studentIdCard);
        studentService.updateStudentCertification(student);
        RegisteredPhone registeredPhone = findRegisteredPhone(student.getPhoneNumber());
        if (!registeredPhone.getHasCertificated()) {
            Point updatedPoint = updatePointService.updatePoint(student, PointType.MISSION_CERTIFICATION_REWARD);
            registeredPhone.updateHasCertificated();
            notificationService.sendAuthorizationNotification(student, updatedPoint,true);
            return StudentVerificationResponse.from(point, PointType.MISSION_CERTIFICATION_REWARD);
        }
        notificationService.sendAuthorizationNotification(student, point,true);
        return StudentVerificationResponse.from(point, PointType.NO_POINT_CHANGE);
    }

    private StudentVerificationResponse rejectStudentIdCard(StudentVerificationRequest request,Point point, StudentIdCard studentIdCard, Student student) {
        studentIdCard.updateRejectionReason(request.rejectionReason());
        notificationService.sendAuthorizationNotification(student, point, false);
        return StudentVerificationResponse.from(point, PointType.NO_POINT_CHANGE);
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

    public void banStudent(Long studentId, BanRequest request) {
        Student student = findStudentService.findByStudentId(studentId);
        student.ban(request.days(), request.banReason());
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

    public void refundPointsToUser(Long roomId, Long reportUserId) {
        Chatroom chatroom = chatroomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        if (chatroom.getType().equals(ChatroomType.MATCHING)) {
            Student reportUser = findStudentService.findByStudentId(reportUserId);
            Point point = updatePointService.updatePoint(reportUser, PointType.CHATROOM_NO_RESPONSE_REFUND);
            notificationService.sendRefundNotification(point, reportUser);
        }
    }

    private RegisteredPhone findRegisteredPhone(String phoneNumber) {
        return registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
    }

    private void rewardIfFirstTime(RegisteredPhone registeredPhone, Student student) {
        if (!registeredPhone.getHasCertificated()) {
            Point point = updatePointService.updatePoint(student, PointType.MISSION_CERTIFICATION_REWARD);
            registeredPhone.updateHasCertificated();
        }
    }
}
