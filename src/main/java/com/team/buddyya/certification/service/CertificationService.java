package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.response.CertificationResponse;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.exception.CertificateExceptionType;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

import static com.team.buddyya.common.domain.S3DirectoryName.STUDENT_ID_CARD;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {

    private final StudentRepository studentRepository;
    private final StudentIdCardRepository studentIdCardRepository;
    private final S3UploadService s3UploadService;

    @Value("${univcert.api.key}")
    private String univcertApiKey;

    public CertificationResponse certificateEmail(StudentInfo studentInfo, EmailCertificationRequest emailRequest) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        if (student.getIsCertificated()) {
            throw new CertificateException(CertificateExceptionType.ALREADY_CERTIFICATED);
        }
        try {
            Map<String, Object> univCertResponse = UnivCert.certify(univcertApiKey, emailRequest.email(), emailRequest.univName(), true);
            boolean isSuccess = (Boolean) univCertResponse.get("success");
            return new CertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new CertificateException(CertificateExceptionType.CERTIFICATE_FAILED);
        }
    }

    public CertificationResponse certificateEmailCode(StudentInfo studentInfo, EmailCodeRequest codeRequest) {
        try {
            Map<String, Object> univCertResponse = UnivCert.certifyCode(univcertApiKey, codeRequest.email(), codeRequest.univName(), codeRequest.code());
            Boolean isSuccess = (Boolean) univCertResponse.get("success");
            if (isSuccess) {
                Student student = studentRepository.findById(studentInfo.id())
                        .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
                student.updateIsCertificated(true);
                studentRepository.save(student);
            }
            UnivCert.clear(univcertApiKey);
            return new CertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new CertificateException(CertificateExceptionType.CERTIFICATE_FAILED);
        }
    }

    public CertificationResponse uploadStudentIdCard(StudentInfo studentInfo, MultipartFile file) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        if (student.getIsCertificated()) {
            throw new CertificateException(CertificateExceptionType.ALREADY_CERTIFICATED);
        }
        String imageUrl = s3UploadService.uploadFile(STUDENT_ID_CARD.getDirectoryName(), file);
        StudentIdCard studentIdCard = StudentIdCard.builder()
                .imageUrl(imageUrl)
                .student(student)
                .build();
        studentIdCardRepository.save(studentIdCard);
        return new CertificationResponse(true);
    }

    public CertificationResponse isCertificated(StudentInfo studentInfo) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        return new CertificationResponse(student.getIsCertificated());
    }
}
