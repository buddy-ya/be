package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.response.EmailCertificationResponse;
import com.team.buddyya.certification.exception.EmailCertificateException;
import com.team.buddyya.certification.exception.EmailCertificateExceptionType;
import com.team.buddyya.common.infrastructure.S3UploadManager;
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

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {

    private final StudentRepository studentRepository;

    @Value("${univcert.api.key}")
    private String univcertApiKey;
    private S3UploadManager s3UploadManager;

    public EmailCertificationResponse certificateEmail(StudentInfo studentInfo, EmailCertificationRequest emailRequest) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        if (student.getIsCertificated()) {
            throw new EmailCertificateException(EmailCertificateExceptionType.ALREADY_CERTIFICATED);
        }
        try {
            Map<String, Object> univCertResponse = UnivCert.certify(univcertApiKey, emailRequest.email(), emailRequest.univName(), true);
            boolean isSuccess = (Boolean) univCertResponse.get("success");
            return new EmailCertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new EmailCertificateException(EmailCertificateExceptionType.EMAIL_CERTIFICATE_FAILED);
        }
    }

    public EmailCertificationResponse certificateEmailCode(StudentInfo studentInfo, EmailCodeRequest codeRequest) {
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
            return new EmailCertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new EmailCertificateException(EmailCertificateExceptionType.EMAIL_CERTIFICATE_FAILED);
        }
    }
}
