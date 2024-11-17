package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.response.EmailCertificationResponse;
import com.team.buddyya.certification.exception.EmailAuthException;
import com.team.buddyya.certification.exception.EmailAuthExceptionType;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.OnBoardingException;
import com.team.buddyya.student.exception.OnBoardingExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {

    private final StudentRepository studentRepository;

    @Value("${univcert.api.key}")
    private String UnivcertApiKey;

    public EmailCertificationResponse certificateEmail(StudentInfo studentInfo, EmailCertificationRequest emailRequest) {
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.STUDENT_NOT_FOUND));
        if (student.getIsCertificated()) {
            throw new EmailAuthException(EmailAuthExceptionType.ALREADY_CERTIFICATED);
        }
        try {
            Map<String, Object> univCertResponse = UnivCert.certify(UnivcertApiKey, emailRequest.email(), emailRequest.univName(), true);
            boolean isSuccess = (Boolean) univCertResponse.get("success");
            return new EmailCertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new EmailAuthException(EmailAuthExceptionType.EMAIL_AUTH_FAILED);
        }
    }

    public EmailCertificationResponse certificateEmailCode(StudentInfo studentInfo, EmailCodeRequest codeRequest) {
        try {
            Map<String, Object> univCertResponse = UnivCert.certifyCode(UnivcertApiKey, codeRequest.email(), codeRequest.univName(), codeRequest.code());
            Boolean isSuccess = (Boolean) univCertResponse.get("success");
            if (isSuccess) {
                Student student = studentRepository.findById(studentInfo.id())
                        .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.STUDENT_NOT_FOUND));
                student.checkIsCertificated(true);
                studentRepository.save(student);
            }
            UnivCert.clear(UnivcertApiKey);
            return new EmailCertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new EmailAuthException(EmailAuthExceptionType.EMAIL_AUTH_FAILED);
        }
    }
}
