package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.request.SendStudentIdCardRequest;
import com.team.buddyya.certification.dto.response.CertificationResponse;
import com.team.buddyya.certification.dto.response.CertificationStatusResponse;
import com.team.buddyya.certification.dto.response.StudentIdCardResponse;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.exception.CertificateExceptionType;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import com.team.buddyya.student.service.StudentService;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.team.buddyya.common.domain.S3DirectoryName.STUDENT_ID_CARD;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {

    private final FindStudentService findStudentService;
    private final StudentIdCardRepository studentIdCardRepository;
    private final S3UploadService s3UploadService;
    private final StudentService studentService;

    @Value("${univcert.api.key}")
    private String univcertApiKey;

    public CertificationResponse certificateEmail(StudentInfo studentInfo, EmailCertificationRequest emailRequest) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        validateStatusAndEmail(emailRequest, student);
        try {
            Map<String, Object> univCertResponse = UnivCert.certify(univcertApiKey, emailRequest.email(), emailRequest.univName(), true);
            boolean isSuccess = (Boolean) univCertResponse.get("success");
            return CertificationResponse.from(isSuccess);
        } catch (IOException e) {
            throw new CertificateException(CertificateExceptionType.CERTIFICATE_FAILED);
        }
    }

    private void validateStatusAndEmail(EmailCertificationRequest emailRequest, Student student) {
        if (student.getIsCertificated()) {
            throw new CertificateException(CertificateExceptionType.ALREADY_CERTIFICATED);
        }
        if (studentService.isDuplicateStudentEmail(emailRequest.email())) {
            throw new CertificateException(CertificateExceptionType.DUPLICATE_EMAIL);
        }
    }

    public CertificationResponse certificateEmailCode(StudentInfo studentInfo, EmailCodeRequest codeRequest) {
        try {
            Map<String, Object> univCertResponse = UnivCert.certifyCode(univcertApiKey, codeRequest.email(), codeRequest.univName(), codeRequest.code());
            Boolean isSuccess = (Boolean) univCertResponse.get("success");
            if (isSuccess) {
                Student student = findStudentService.findByStudentId(studentInfo.id());
                updateCertification(codeRequest, student);
            }
            UnivCert.clear(univcertApiKey);
            return CertificationResponse.from(isSuccess);
        } catch (IOException e) {
            throw new CertificateException(CertificateExceptionType.CERTIFICATE_FAILED);
        }
    }

    private void updateCertification(EmailCodeRequest codeRequest, Student student) {
        student.updateIsCertificated(true);
        student.updateEmail(codeRequest.email());
    }

    public CertificationResponse uploadStudentIdCard(StudentInfo studentInfo, SendStudentIdCardRequest sendStudentIdCardRequest) {
        MultipartFile file = sendStudentIdCardRequest.image();
        Student student = findStudentService.findByStudentId(studentInfo.id());
        if (student.getIsCertificated()) {
            throw new CertificateException(CertificateExceptionType.ALREADY_CERTIFICATED);
        }
        String imageUrl = s3UploadService.uploadFile(STUDENT_ID_CARD.getDirectoryName(), file);
        Optional<StudentIdCard> existStudentIdCard = studentIdCardRepository.findByStudent(student);
        if (existStudentIdCard.isPresent()) {
            return updateExistingStudentIdCard(imageUrl, existStudentIdCard.get());
        }
        StudentIdCard studentIdCard = StudentIdCard.builder()
                .imageUrl(imageUrl)
                .student(student)
                .build();
        studentIdCardRepository.save(studentIdCard);
        return CertificationResponse.from(true);
    }

    private CertificationResponse updateExistingStudentIdCard(String imageUrl, StudentIdCard existStudentIdCard) {
        s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), existStudentIdCard.getImageUrl());
        existStudentIdCard.updateImageUrl(imageUrl);
        return CertificationResponse.from(true);
    }

    public CertificationStatusResponse isCertificated(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        return CertificationStatusResponse.from(student, isStudentIdCardRequested);
    }

    public StudentIdCardResponse getStudentIdCard(StudentInfo studentInfo) {
        StudentIdCard studentIdCard = studentIdCardRepository.findByStudent_Id(studentInfo.id())
                .orElseThrow(() -> new CertificateException(CertificateExceptionType.STUDENT_ID_CARD_NOT_FOUND));
        return StudentIdCardResponse.from(studentIdCard.getImageUrl());
    }
}
