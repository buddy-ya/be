package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.certification.domain.StudentEmail;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.request.SendStudentIdCardRequest;
import com.team.buddyya.certification.dto.response.CertificationResponse;
import com.team.buddyya.certification.dto.response.CertificationStatusResponse;
import com.team.buddyya.certification.dto.response.StudentIdCardResponse;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.exception.CertificateExceptionType;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.repository.RegisteredPhoneRepository;
import com.team.buddyya.certification.repository.StudentEmailRepository;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.service.FindPointService;
import com.team.buddyya.point.service.UpdatePointService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.team.buddyya.common.domain.S3DirectoryName.STUDENT_ID_CARD;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {

    private final FindStudentService findStudentService;
    private final StudentIdCardRepository studentIdCardRepository;
    private final StudentEmailRepository studentEmailRepository;
    private final S3UploadService s3UploadService;
    private final RegisteredPhoneRepository registeredPhoneRepository;
    private final UpdatePointService updatePointService;
    private final FindPointService findPointService;

    public CertificationResponse certificateEmailCode(StudentInfo studentInfo, EmailCodeRequest codeRequest) {
        StudentEmail studentEmail = studentEmailRepository.findByEmail(codeRequest.email())
                .orElseThrow(() -> new CertificateException(CertificateExceptionType.STUDENT_EMAIL_NOT_FOUND));
        if (!codeRequest.code().equals(studentEmail.getAuthenticationCode())) {
            throw new CertificateException(CertificateExceptionType.CODE_MISMATCH);
        }
        Student student = findStudentService.findByStudentId(studentInfo.id());
        CertificationResponse response = applyCertification(codeRequest, student);
        return response;
    }

    public void saveCode(String email, String generatedCode) {
        StudentEmail studentEmail = studentEmailRepository.findByEmail(email)
                .orElseGet(() -> new StudentEmail(email, generatedCode));
        if (studentEmail.getId() != null) {
            studentEmail.updateAuthenticationCode(generatedCode);
        }
        studentEmailRepository.save(studentEmail);
    }

    private CertificationResponse applyCertification(EmailCodeRequest codeRequest, Student student) {
        student.updateIsCertificated(true);
        student.updateEmail(codeRequest.email());
        RegisteredPhone registeredPhone = findRegisteredPhone(student.getPhoneNumber());
        return rewardIfFirstTime(registeredPhone, student);
    }

    public void uploadStudentIdCard(StudentInfo studentInfo,
                                                     SendStudentIdCardRequest sendStudentIdCardRequest) {
        MultipartFile file = sendStudentIdCardRequest.image();
        Student student = findStudentService.findByStudentId(studentInfo.id());
        if (student.getIsCertificated()) {
            throw new CertificateException(CertificateExceptionType.ALREADY_CERTIFICATED);
        }
        String imageUrl = s3UploadService.uploadFile(STUDENT_ID_CARD.getDirectoryName(), file);
        Optional<StudentIdCard> existStudentIdCard = studentIdCardRepository.findByStudent(student);
        if (existStudentIdCard.isPresent()) {
            updateExistingStudentIdCard(imageUrl, existStudentIdCard.get());
            return;
        }
        StudentIdCard studentIdCard = StudentIdCard.builder()
                .imageUrl(imageUrl)
                .student(student)
                .build();
        studentIdCardRepository.save(studentIdCard);
    }

    private void updateExistingStudentIdCard(String imageUrl, StudentIdCard existStudentIdCard) {
        s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), existStudentIdCard.getImageUrl());
        existStudentIdCard.updateImageUrl(imageUrl);
    }

    @Transactional(readOnly = true)
    public CertificationStatusResponse isCertificated(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        return CertificationStatusResponse.from(student, isStudentIdCardRequested);
    }

    @Transactional(readOnly = true)
    public StudentIdCardResponse getStudentIdCard(StudentInfo studentInfo) {
        StudentIdCard studentIdCard = studentIdCardRepository.findByStudent_Id(studentInfo.id())
                .orElseThrow(() -> new CertificateException(CertificateExceptionType.STUDENT_ID_CARD_NOT_FOUND));
        return StudentIdCardResponse.from(studentIdCard.getImageUrl(), studentIdCard.getRejectionReason());
    }

    public void refreshStudentCertification(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        student.updateIsCertificated(false);
        student.updateEmail(null);
        studentIdCardRepository.deleteByStudent(student);
    }

    private RegisteredPhone findRegisteredPhone(String phoneNumber) {
        return registeredPhoneRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneAuthenticationException(PhoneAuthenticationExceptionType.PHONE_NOT_FOUND));
    }

    private CertificationResponse rewardIfFirstTime(RegisteredPhone registeredPhone, Student student) {
        if(!registeredPhone.getHasCertificated()){
            Point point = updatePointService.updatePoint(student, PointType.MISSION_CERTIFICATION_REWARD);
            registeredPhone.updateHasCertificated();
            return CertificationResponse.from(point, PointType.MISSION_CERTIFICATION_REWARD);
        }
        Point point = findPointService.findByStudent(student);
        return CertificationResponse.from(point, PointType.NO_POINT_CHANGE);
    }
}
